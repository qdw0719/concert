package com.hb.concert.infrastructure.concert;

import com.hb.concert.domain.concert.ConcertDetail;
import com.hb.concert.domain.common.enumerate.ValidState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ConcertDetailJpaRepository extends JpaRepository<ConcertDetail, Long> {
    List<ConcertDetail> findByConcertIdAndConcertDateAfterAndValidState(String concertId, LocalDate currentDate, ValidState validState);

    @Query("select distinct cd.concertId from ConcertDetail cd where cd.concertDate > :currentDate")
    List<String> findDistinctConcertIdByConcertDateAfter(LocalDate currentDate);

    ConcertDetail findByConcertId(String concertId);

    int countByConcertIdAndConcertDetailId(String concertId, String detailId);
}
