package com.hb.concert.infrastructure.reservation;

import com.hb.concert.domain.reservation.ReservationDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReservationDetailJpaRepository extends JpaRepository<ReservationDetail, Long> {

    @Query("select rd.concertSeatId from ReservationDetail rd where rd.reservationId = :reservationId")
    List<Integer> findConcertSeatIdByReservationId(String reservationId);

    List<ReservationDetail> findByReservationId(String reservationId);
}
