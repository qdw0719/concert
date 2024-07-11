package com.hb.concert.infrastructure.concert;

import com.hb.concert.domain.concert.Concert;
import com.hb.concert.domain.concert.ConcertRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ConcertRepositoryImpl implements ConcertRepository {

    private final ConcertJpaRepository concertJpaRepository;

    public ConcertRepositoryImpl(ConcertJpaRepository concertJpaRepository) {
        this.concertJpaRepository = concertJpaRepository;
    }

    @Override
    public Concert findByConcertId(String concertId) {
        return concertJpaRepository.findByConcertId(concertId);
    }

    @Override
    public List<Concert> saveAll(List<Concert> concertList) {
        return concertJpaRepository.saveAll(concertList);
    }

    @Override
    public int count() {
        return (int) concertJpaRepository.count();
    }

    @Override
    public List<Concert> findAll() {
        return concertJpaRepository.findAll();
    }
}
