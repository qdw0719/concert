package com.hb.concert.domain.concert;

import com.hb.concert.application.concert.command.ConcertCommand;

import java.util.List;

public interface ConcertSeatRepository {
//    List<ConcertSeat> findByConcertIdAndUseYn(String concertId, UseYn useYn);

    List<ConcertSeat> findByConcertIdAndConcertDetailId(String concertId, String concertDetailId);

    ConcertSeat save(ConcertSeat concertSeat);

    List<ConcertSeat> saveAll(List<ConcertSeat> concertSeatList);

    int count();

    ConcertSeat findByConcertIdAndConcertDetailIdAndConcertSeatId(String concertId, String detailId, Integer seatId);
}
