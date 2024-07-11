package com.hb.concert.infrastructure.reservation;

import com.hb.concert.domain.reservation.ReservationDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationDetailJpaRepository extends JpaRepository<ReservationDetail, Long> {

    List<Integer> findConcertSeatIdByReservationId(String reservationId);
}
