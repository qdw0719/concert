package com.hb.concert.infrastructure.reservation;

import com.hb.concert.domain.reservation.ReservationDetail;
import com.hb.concert.domain.reservation.ReservationDetailRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ReservationDetailRepositoryimpl implements ReservationDetailRepository {

    private final ReservationDetailJpaRepository reservationDetailJpaRepository;;

    public ReservationDetailRepositoryimpl(ReservationDetailJpaRepository reservationDetailJpaRepository) {
        this.reservationDetailJpaRepository = reservationDetailJpaRepository;
    }

    @Override
    public ReservationDetail save(ReservationDetail detail) {
        return reservationDetailJpaRepository.save(detail);
    }

    @Override
    public List<ReservationDetail> saveAll(List<ReservationDetail> reservationDetailList) {
        return reservationDetailJpaRepository.saveAll(reservationDetailList);
    }

    @Override
    public List<Integer> findConcertSeatIdByReservationId(String reservationId) {
        return reservationDetailJpaRepository.findConcertSeatIdByReservationId(reservationId);
    }
}
