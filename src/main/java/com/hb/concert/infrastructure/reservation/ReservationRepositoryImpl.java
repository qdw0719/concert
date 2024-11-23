package com.hb.concert.infrastructure.reservation;

import com.hb.concert.domain.common.enumerate.UseYn;
import com.hb.concert.domain.reservation.Reservation;
import com.hb.concert.domain.reservation.ReservationRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public class ReservationRepositoryImpl implements ReservationRepository {

    private ReservationJpaRepository reservationJpaRepository;

    public ReservationRepositoryImpl(ReservationJpaRepository reservationJpaRepository) {
        this.reservationJpaRepository = reservationJpaRepository;
    }

    @Override
    public Reservation save(Reservation reservation) {
        return reservationJpaRepository.save(reservation);
    }

    @Override
    public Reservation findByReservationId(String reservationId) {
        return reservationJpaRepository.findByReservationId(reservationId);
    }

    @Override
    public int countByReservationId(String sreservationId) {
        return reservationJpaRepository.countByReservationId(sreservationId);
    }

    @Override
    public Reservation findTopByOrderByIdDesc() {
        return reservationJpaRepository.findTopByOrderByIdDesc();
    }

    @Override
    public List<Reservation> findByIsPaidAndReservationTimeBefore(UseYn useYn, LocalDateTime localDateTime) {
        return reservationJpaRepository.findByIsPaidAndReservationTimeBefore(useYn, localDateTime);
    }

    @Override
    public List<Reservation> findByIsPaidAndTemporaryGrantTimeBefore(UseYn useYn, LocalDateTime now) {
        return reservationJpaRepository.findByIsPaidAndTemporaryGrantTimeBefore(useYn, now);
    }

    @Override
    public Reservation findTopByOrderByUserIdDesc(UUID userId) {
        return reservationJpaRepository.findTopByOrderByUserIdDesc(userId);
    }
}