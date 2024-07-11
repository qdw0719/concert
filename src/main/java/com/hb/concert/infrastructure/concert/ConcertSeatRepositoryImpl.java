package com.hb.concert.infrastructure.concert;

import com.hb.concert.application.concert.command.ConcertCommand;
import com.hb.concert.domain.concert.ConcertSeat;
import com.hb.concert.domain.concert.ConcertSeatRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ConcertSeatRepositoryImpl implements ConcertSeatRepository {

    private final ConcertSeatJpaRepository concertSeatJpaRepository;

    public ConcertSeatRepositoryImpl(ConcertSeatJpaRepository concertSeatJpaRepository) {
        this.concertSeatJpaRepository = concertSeatJpaRepository;
    }

//    @Override
//    public List<ConcertSeat> findByConcertIdAndUseYn(String concertId, UseYn useYn) {
//        return concertSeatJpaRepository.findByConcertIdAndUseYn(concertId, useYn);
//    }

    @Override
    public List<ConcertSeat> findByConcertIdAndConcertDetailId(String concertId, String concertDetailId) {
        return concertSeatJpaRepository.findByConcertIdAndConcertDetailId(concertId, concertDetailId);
    }

    @Override
    public ConcertSeat save(ConcertSeat concertSeat) {
        return concertSeatJpaRepository.save(concertSeat);
    }

    @Override
    public List<ConcertSeat> saveAll(List<ConcertSeat> concertSeatList) {
        return concertSeatJpaRepository.saveAll(concertSeatList);
    }

    @Override
    public int count() {
        return (int) concertSeatJpaRepository.count();
    }
}
