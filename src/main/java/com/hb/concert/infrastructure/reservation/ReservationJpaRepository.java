package com.hb.concert.infrastructure.reservation;

import com.hb.concert.domain.common.enumerate.UseYn;
import com.hb.concert.domain.reservation.Reservation;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReservationJpaRepository extends JpaRepository<Reservation, Long> {
    Reservation findByReservationId(String reservationId);

    int countByReservationId(String reservationId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Reservation r ORDER BY r.id DESC")
    Optional<Reservation> findTopByOrderByIdDesc();

    List<Reservation> findByIsPaidAndReservationTimeBefore(UseYn useYn, LocalDateTime localDateTime);

    Reservation findTopByUserIdOrderByUserIdDesc(UUID userId);

    List<Reservation> findByIsPaidAndTemporaryGrantTimeBefore(UseYn useYn, LocalDateTime now);

    List<Reservation> findAllByIsPaid(UseYn useYn);

    @Query("select r from Reservation r where r.userId = :userId and r.reservationTime between :min and :max")
    Reservation findTopByUserIdAndReservationTimeBetweenOrderByUserIdDesc(UUID userId, LocalDateTime min, LocalDateTime max);

    @Query("SELECT r.userId FROM Reservation r WHERE r.reservationTime BETWEEN :startTime AND :endTime")
    List<UUID> findUserNotReservationToday(LocalDateTime startTime, LocalDateTime endTime);

    @Query("SELECT r FROM Reservation r WHERE r.userId = :userId ORDER BY r.reservationTime DESC")
    Reservation findTopByUserIdOrderByReservationTimeDesc(UUID userId);
}
