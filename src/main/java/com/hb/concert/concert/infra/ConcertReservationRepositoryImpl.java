package com.hb.concert.concert.infra;

import com.hb.concert.concert.entity.ConcertReservation;
import com.hb.concert.concert.entity.repository.ConcertReservationRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class ConcertReservationRepositoryImpl implements ConcertReservationRepository {

    private final ConcertReservationJpaRepository concertReservationJpaRepository;

    public ConcertReservationRepositoryImpl(ConcertReservationJpaRepository concertReservationJpaRepository) {
        this.concertReservationJpaRepository = concertReservationJpaRepository;
    }

    @Override public void save(ConcertReservation concertReservation) {
        concertReservationJpaRepository.save(concertReservation);
    }

    @Override public List<ConcertReservation> getReservationNotPaid() {
        return concertReservationJpaRepository.findByNotIsPaid();
    }

    @Override public void saveAll(List<ConcertReservation> concertReservations) {
        concertReservationJpaRepository.saveAll(concertReservations);
    }

    @Override public ConcertReservation getReservationInfoByReservationId(UUID userId, String reservationId) {
        return concertReservationJpaRepository.findByUserIdAndReservationId(userId, reservationId);
    }

    @Override public List<ConcertReservation> getReservationInfo(String reservationId) {
        return concertReservationJpaRepository.findAllByReservationId(reservationId);
    }
}
