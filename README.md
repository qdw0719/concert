## 동시성 이슈가 발생될만한 usecase

---

### 1. 유저의 잔액충전

- 다중요청(흔히 따닥이라 하는..)으로 인한 유저 잔액의 중복처리 방지
- 1회를 제외한 모든 요청은 실패해야 하기에 **낙관락**을 선택.

### 2. 예약 시 좌석 선점

- 동일좌석을 여러명이 신청할 때 가장 빠른요청에 좌석 임시배정
- 예약요청 → 가장 최근의 예약번호 조회 → seq증가하여 새로운 예약번호 생성 → ... → 예약처리
- 가장 최근 예약을 조회할 때 다른 요청들도 같은 예약번호를 보게되면 seq증가 시 동일한 예약번호가 생성되므로 **비관락** 선택
- 동일한 좌석을 예약신청 했을 때 한 명만 예약에 성공해야 하기때문에 예약에는 **낙관락** 선택.


### 3. 이슈

- 예약에서 처음 낙관락만 적용하였을 때 데이터 중복 에러가 발생. (예약id 는 unique 를 걸어두었음)
- 3번째의 이유로 중복에러가 발생했으므로 가장 마지막의 예약번호를 조회할 때 비관락을 걸고 다시 테스트
- deadlock 발생
``` java
2024-07-25T22:49:01.457+09:00 ERROR 7208 ---
[onPool-worker-2] o.h.engine.jdbc.spi.SqlExceptionHelper   :
Deadlock found when trying to get lock; try restarting transaction
```
- 이후 해결하지 못한채로 마무리...

---

### Reservation.java
```java
// import 생략
// 기타 annotation 생략
public class Reservation {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("예약번호") @Column(nullable = false, unique = true)
    private String reservationId;

    @Comment("유저 UUID") @Column(nullable = false)
    private UUID userId;

    // .... 기타 등등

    @Version
    private int version;

    // .... 생략
}

```

 ### ReservationService.java
```java
// ..... 다른메소드 생략
 @Transactional(isolation = Isolation.SERIALIZABLE)
public Reservation createReservation(ReservationCommand.Create command) {
    Reservation reservation = Reservation.create(generateReservationId(), command.userId(), command.concertId(), command.concertDetailId(), 5);
    saveReservation(reservation);
    return reservation;
}

// ..... 다른메소드 생략

 // 문제의 비관락걸린 쿼리가 있는 메서드
 private String generateReservationId() {
    String reservationIdStartStr = "reservation_";
    String newReservationId;
    Optional<Reservation> getLastReservation = reservationRepository.findTopByOrderByIdDesc();

    if (getLastReservation.isPresent()) {
       String lastReservationId = getLastReservation.map(Reservation::getReservationId).get();
       int nextId = Integer.parseInt(lastReservationId.split("_")[1]) + 1;
       newReservationId = reservationIdStartStr + String.format("%04d", nextId);
    } else {
       newReservationId = "reservation_0001";
    }
    return newReservationId;
 }
 
```

### ReservationJpaRepository.java
```java
// .... 생략
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT r FROM Reservation r ORDER BY r.id DESC")
Optional<Reservation> findTopByOrderByIdDesc();
// .... 생략
```



