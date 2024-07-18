package com.hb.concert.infrastructure.concert;

import com.hb.concert.domain.concert.Concert;
import com.hb.concert.domain.concert.ConcertDetail;
import com.hb.concert.domain.concert.ConcertDetailRepository;
import com.hb.concert.domain.common.enumerate.ValidState;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class ConcertDetailRepositoryImpl implements ConcertDetailRepository {

    private final ConcertDetailJpaRepository concertDetailJpaRepository;

    public ConcertDetailRepositoryImpl(ConcertDetailJpaRepository concertDetailJpaRepository) {
        this.concertDetailJpaRepository = concertDetailJpaRepository;
    }

    @Override
    public void saveAll(List<ConcertDetail> concertDetailList) {
        concertDetailJpaRepository.saveAll(concertDetailList);
    }

    @Override
    public List<ConcertDetail> findByConcertIdAndConcertDateAfterAndValidState(String concertId, LocalDate currentDate, ValidState validState) {
        return concertDetailJpaRepository.findByConcertIdAndConcertDateAfterAndValidState(concertId, currentDate, validState);
    }

    @Override
    public List<String> findDistinctConcertIdByConcertDateAfter(LocalDate currentDate) {
        return concertDetailJpaRepository.findDistinctConcertIdByConcertDateAfter(currentDate);
    }

    @Override
    public List<ConcertDetail> findAll() {
        return concertDetailJpaRepository.findAll();
    }

    @Override
    public int count() {
        return (int) concertDetailJpaRepository.count();
    }

    @Override
    public ConcertDetail findByConcertId(String concertId) {
        return concertDetailJpaRepository.findByConcertId(concertId);
    }

    @Override
    public int countByConcertIdAndConcertDetailId(String concertId, String detailId) {
        return concertDetailJpaRepository.countByConcertIdAndConcertDetailId(concertId, detailId);
    }
}
