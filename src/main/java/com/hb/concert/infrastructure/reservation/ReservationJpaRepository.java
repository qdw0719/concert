package com.hb.concert.infrastructure.reservation;

import com.hb.concert.domain.common.enumerate.UseYn;
import com.hb.concert.domain.reservation.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ReservationJpaRepository extends JpaRepository<Reservation, Long> {
    Reservation findByReservationId(String reservationId);

    int countByReservationId(String sreservationId);

    Reservation findTopByOrderByIdDesc();

    List<Reservation> findByIsPaidAndReservationTimeBefore(UseYn useYn, LocalDateTime localDateTime);

    @Query("SELECT r FROM Reservation r WHERE r.userId = :userId ORDER BY r.userId DESC")
    Reservation findTopByOrderByUserIdDesc(@Param("userId") UUID userId);

    List<Reservation> findByIsPaidAndTemporaryGrantTimeBefore(UseYn useYn, LocalDateTime now);
}
