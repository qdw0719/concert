package com.hb.concert.domain.concert;

import com.hb.concert.domain.common.enumerate.ValidState;

import java.time.LocalDate;
import java.util.List;

public interface ConcertDetailRepository {
    void saveAll(List<ConcertDetail> concertDetails);

    List<ConcertDetail> findByConcertIdAndConcertDateAfterAndValidState(String concertId, LocalDate currentDate, ValidState validState);

    List<Concert> findDistinctConcertIdByConcertDateAfter(LocalDate currentDate);

    List<ConcertDetail> findAll();

    int count();

    ConcertDetail findByConcertId(String concertId);

    int countByConcertIdAndDetailId(String concertId, String detailId);
}
