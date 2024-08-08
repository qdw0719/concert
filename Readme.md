## 1. 테이블 개요

- Concert
- CocnertDetail
- ConcertReservation
- ConcertSeatConfig
- Payment
- User
- QueueToken(redis로 사용하며 현재 사용하지 않음)

## 2. 인덱스가 필요한 테이블과 이유

### 2.1. ConcertDetail

- **이유:** 시간이 지나면서 콘서트의 종류와 일정 데이터가 많이 축적될 수 있다. 특히 콘서트의 종류와 일정에 따라 데이터를 조회할 일이 많으므로 인덱스가 필요
- **설정된 인덱스:** concertDate

### 2.2. ConcertReservation

- **이유:** 한 콘서트의 수용 인원(capacity)과 콘서트 수를 고려했을 때, 데이터가 많이 축적될 가능성이 크다. 특히 콘서트 예약 시간과 예약 상세 ID로 자주 조회가 이루어질 수 있으므로 필요
- **설정된 인덱스:** concertDetailId, reservationTime


### 2.3. Payment

- **이유:** 예약 확정 수를 기준으로 데이터를 조회할 일이 많을 것이므로 인덱스가 필요
- **설정된 인덱스:** reservationId

## 3. 인덱스 지정에 대한 이유

### ConcertDetail 테이블

- concertId: 특정 콘서트의 모든 상세 정보를 조회할 때 효율성을 높일 수 있다
- concertDate: 특정 날짜의 콘서트 정보를 조회할 때 조회성능을 높일 수 있다


| Table | Non\_unique | Key\_name | Seq\_in\_index | Column\_name | Collation | Cardinality | Sub\_part | Packed | Null | Index\_type | Comment | Index\_comment | Visible | Expression |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| hb\_concert\_detail | 0 | PRIMARY | 1 | id | A | 10188 | null | null |  | BTREE |  |  | YES | null |
| hb\_concert\_detail | 1 | idx\_concert\_detail | 1 | concert\_id | A | 3 | null | null | YES | BTREE |  |  | YES | null |
| hb\_concert\_detail | 1 | idx\_concert\_detail | 2 | concert\_date | A | 9394 | null | null | YES | BTREE |  |  | YES | null |

- 카디널리티를 보았을 때 ConcertDetail 테이블의 인덱스는 concert_date 컬럼만 있으면 될 것으로 보이므로 최종적으로 지정 할 인덱스는 `concert_date`



### ConcertReservation 테이블

- concertDetailId: 특정 콘서트의 예약 정보를 조회할 때 유용하다.
- reservationTime: 예약 시간에 따라 예약 정보를 정렬하거나 검색할 때 효율적이다


| Table | Non\_unique | Key\_name | Seq\_in\_index | Column\_name | Collation | Cardinality | Sub\_part | Packed | Null | Index\_type | Comment | Index\_comment | Visible | Expression |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| hb\_concert\_reservation | 0 | PRIMARY | 1 | id | A | 9784 | null | null |  | BTREE |  |  | YES | null |
| hb\_concert\_reservation | 1 | idx\_concert\_reservation | 1 | concert\_detail\_id | A | 1000 | null | null | YES | BTREE |  |  | YES | null |
| hb\_concert\_reservation | 1 | idx\_concert\_reservation | 2 | reservation\_time | A | 9784 | null | null | YES | BTREE |  |  | YES | null |

- 카디널리티가 reservation_time - concertDetailId 순이므로 `인덱스의 순서`는 `reservation_time - concertDetailId` 순으로 지정



### Payment 테이블

- reservationId: 특정 예약의 결제 정보를 조회할 때 빠르게 접근할 수 있게 한다


| Table | Non\_unique | Key\_name | Seq\_in\_index | Column\_name | Collation | Cardinality | Sub\_part | Packed | Null | Index\_type | Comment | Index\_comment | Visible | Expression |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| hb\_payment | 0 | PRIMARY | 1 | id | A | 10048 | null | null |  | BTREE |  |  | YES | null |
| hb\_payment | 1 | idx\_payment | 1 | reservation\_id | A | 10000 | null | null | YES | BTREE |  |  | YES | null |

- 결제테이블에는 예약번호 이외에 인덱스는 설정하지 않으므로 그대로 진행


## 4. 인덱스 적용 전과 후 비교

### 3-1.  비교쿼리
```SQL
explain analyze
select * from hb_concert_detail where concert_date between date_sub(now(), interval 100 day) and now();
```
- 인덱스 적용 전(explain analyze)
```text
-> Filter: (hb_concert_detail.concert_date between <cache>((now() - interval 100 day)) and <cache>(now()))  
(cost=134 rows=1102) (actual time=0.147..2.18 rows=20 loops=1)
-> Table scan on hb_concert_detail  (cost=134 rows=9922) (actual time=0.0513..1.87 rows=10000 loops=1)
```
- 인덱스 적용 후(explain analyze)
```text
-> Index range scan on hb_concert_detail using idx_concert_detail over ('2024-04-29' < concert_date <= '2024-08-07'), with index condition: (hb_concert_detail.concert_date between <cache>((now() - interval 100 day)) and <cache>(now()))  
(cost=7.91 rows=17) (actual time=0.0349..0.257 rows=17 loops=1)
```

### 3-2.  비교쿼리
```SQL
select * from hb_concert_reservation where concert_detail_id = 'concertDetail3' and reservation_time < now();
```
- 인덱스 적용 전(explain analyze)
```text
-> Filter: ((hb_concert_reservation.concert_detail_id = 'concertDetail3') and (hb_concert_reservation.reservation_time < <cache>(now())))  
(cost=968 rows=337) (actual time=0.049..4.89 rows=10 loops=1)
-> Table scan on hb_concert_reservation  (cost=968 rows=10111) (actual time=0.0411..3.41 rows=10000 loops=1)
```
- 인덱스 적용 후(explain analyze)
```text
-> Index range scan on hb_concert_reservation using idx_concert_reservation over (concert_detail_id = 'concertDetail3' AND NULL < reservation_time < '2024-08-07 23:20:32.000000'), 
with index condition: ((hb_concert_reservation.concert_detail_id = 'concertDetail3') and (hb_concert_reservation.reservation_time < <cache>(now())))  
(cost=4.76 rows=10) (actual time=0.0326..0.0733 rows=10 loops=1)
```



### 3-3.  비교쿼리
```SQL
select * from hb_payment where reservation_id = 'reservation17';
```
- 인덱스 적용 전(explain analyze)
```text
-> Filter: (hb_payment.reservation_id = 'reservation17')  
(cost=995 rows=971) (actual time=0.0717..3.43 rows=1 loops=1)
-> Table scan on hb_payment  (cost=995 rows=9710) (actual time=0.0653..2.18 rows=10000 loops=1)
```
- 인덱스 적용 후(explain analyze)
```text
-> Index lookup on hb_payment using idx_payment (reservation_id='reservation17')  
(cost=0.35 rows=1) 
(actual time=0.029..0.0305 rows=1 loops=1)
```

