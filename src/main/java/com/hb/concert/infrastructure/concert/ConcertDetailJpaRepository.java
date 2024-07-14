package com.hb.concert.infrastructure.concert;

import com.hb.concert.domain.concert.Concert;
import com.hb.concert.domain.concert.ConcertDetail;
import com.hb.concert.domain.common.enumerate.ValidState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ConcertDetailJpaRepository extends JpaRepository<ConcertDetail, Long> {
    List<ConcertDetail> findByConcertIdAndConcertDateAfterAndValidState(String concertId, LocalDate currentDate, ValidState validState);

    List<Concert> findDistinctConcertIdByConcertDateAfter(LocalDate currentDate);

    ConcertDetail findByConcertId(String concertId);

    int countByConcertIdAndDetailId(String concertId, String detailId);
}
