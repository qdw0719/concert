package com.hb.concert.domain.concert.repository;

import com.hb.concert.domain.concert.ConcertReservation;

import java.util.List;
import java.util.UUID;

public interface ConcertReservationRepository {
    void save(ConcertReservation concertReservation);

    List<ConcertReservation> getReservationNotPaid();

    void saveAll(List<ConcertReservation> concertReservations);

    ConcertReservation getReservationInfoByReservationId(UUID userId, String reservationId);

    List<ConcertReservation> getReservationInfo(String reservationId);
}
