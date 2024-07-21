package com.hb.concert.infrastructure.reservation;

import com.hb.concert.domain.common.enumerate.UseYn;
import com.hb.concert.domain.reservation.Reservation;
import com.hb.concert.domain.reservation.ReservationRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
    public int countByReservationId(String reservationId) {
        return reservationJpaRepository.countByReservationId(reservationId);
    }

    @Override
    public Optional<Reservation> findTopByOrderByIdDesc() {
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
    public Reservation findTopByUserIdOrderByUserIdDesc(UUID userId) {
        return reservationJpaRepository.findTopByUserIdOrderByUserIdDesc(userId);
    }

    @Override
    public Reservation findTopByUserIdAndReservationTimeBetweenOrderByUserIdDesc(UUID userId, LocalDateTime min, LocalDateTime max) {
        return reservationJpaRepository.findTopByUserIdAndReservationTimeBetweenOrderByUserIdDesc(userId, min, max);
    }

    @Override
    public List<Reservation> findAllByIsPaid(UseYn useYn) {
        return reservationJpaRepository.findAllByIsPaid(useYn);
    }

    @Override
    public List<UUID> findUserNotReservationToday(LocalDateTime startTime, LocalDateTime endTime) {
        return reservationJpaRepository.findUserNotReservationToday(startTime, endTime);
    }
}