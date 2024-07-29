package com.hb.concert.infrastructure.concert;

import com.hb.concert.domain.concert.ConcertDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ConcertDetailJpaRepository extends JpaRepository<ConcertDetail, Long> {

    @Query("select cd from ConcertDetail cd where cd.concertDate >= CURRENT_TIMESTAMP")
    List<ConcertDetail> findByAvailableConcerts();

    @Query("select cd from ConcertDetail cd where cd.concertId = :concertId and cd.concertDetailId = :concertDetailId and cd.availableSeatCount > 0")
    Optional<ConcertDetail> findByConcertIdAndConcertDetailId(String concertId, String concertDetailId);

    Optional<ConcertDetail> findByConcertDetailId(String concertDetailId);
}
