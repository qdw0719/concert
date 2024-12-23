package com.hb.concert.concert.infra;

import com.hb.concert.concert.entity.ConcertReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ConcertReservationJpaRepository extends JpaRepository<ConcertReservation, Long> {

    @Query("select cr from ConcertReservation cr where cr.isPaid = 'N'")
    List<ConcertReservation> findByNotIsPaid();

    ConcertReservation findByUserIdAndReservationId(UUID userId, String reservationId);

    List<ConcertReservation> findAllByReservationId(String reservationId);
}
