package com.hb.concert.domain.reservation;

import java.util.List;

public interface ReservationDetailRepository {

    ReservationDetail save(ReservationDetail detail);

    List<ReservationDetail> saveAll(List<ReservationDetail> reservationDetailList);

    List<Integer> findConcertSeatIdByReservationId(String reservationId);

    List<ReservationDetail> findByReservationId(String reservationId);

    List<ReservationDetail> getReservationDetailInfo(String reservationId);
}
