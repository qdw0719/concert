package com.hb.concert.domain.reservation;

import com.hb.concert.domain.common.enumerate.UseYn;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ReservationRepository {
    Reservation save(Reservation reservation);

    Reservation findByReservationId(String reservationId);

    int countByReservationId(String sreservationId);

    Reservation findTopByOrderByIdDesc();

    List<Reservation> findByIsPaidAndReservationTimeBefore(UseYn useYn, LocalDateTime localDateTime);

    List<Reservation> findByIsPaidAndTemporaryGrantTimeBefore(UseYn useYn, LocalDateTime now);

    Reservation findTopByOrderByUserIdDesc(UUID userId);
}
