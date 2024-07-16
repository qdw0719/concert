package com.hb.concert.infrastructure.reservation;

import com.hb.concert.domain.common.enumerate.UseYn;
import com.hb.concert.domain.reservation.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ReservationJpaRepository extends JpaRepository<Reservation, Long> {
    Reservation findByReservationId(String reservationId);

    int countByReservationId(String sreservationId);

    Reservation findTopByOrderByIdDesc();

    List<Reservation> findByIsPaidAndReservationTimeBefore(UseYn useYn, LocalDateTime localDateTime);

    Reservation findTopByUserIdOrderByUserIdDesc(UUID userId);

    List<Reservation> findByIsPaidAndTemporaryGrantTimeBefore(UseYn useYn, LocalDateTime now);

    List<Reservation> findAllByIsPaid(UseYn useYn);

    Reservation findTopByUserIdAndReservationTimeBetweenOrderByUserIdDesc(UUID userId, LocalDateTime min, LocalDateTime max);
}
