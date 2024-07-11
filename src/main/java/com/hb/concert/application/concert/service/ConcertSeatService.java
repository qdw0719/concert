package com.hb.concert.application.concert.service;

import com.hb.concert.application.concert.command.ConcertCommand;
import com.hb.concert.domain.concert.ConcertSeat;
import com.hb.concert.domain.concert.ConcertSeatRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConcertSeatService {

    private final ConcertSeatRepository concertSeatRepository;

    public ConcertSeatService(ConcertSeatRepository concertSeatRepository) {
        this.concertSeatRepository = concertSeatRepository;
    }

    /**
     * 특정 콘서트의 예약 가능한 좌석 목록을 조회
     *
     * @param command  콘서트 ID, detailId 일정ID
     * @return 예약 가능한 콘서트 좌석 목록
     */
//    public List<ConcertSeat> findAvailableSeats(String concertId) {
//        return concertSeatRepository.findByConcertIdAndUseYn(concertId, UseYn.Y);
//    }

    public List<ConcertSeat> findConcertSeats(ConcertCommand.getConcertSeat command) {
        return concertSeatRepository.findByConcertIdAndConcertDetailId(command.concertId(), command.detailId());
    }


    public ConcertSeat saveConcertSeat(ConcertCommand.saveConcertSeat command) {
        ConcertSeat concertSeat = new ConcertSeat().builder()
                .concertSeatId(command.concertSeatId())
                .concertDetailId(command.concertDetailId())
                .concertId(command.concertId())
                .useYn(command.useYn())
                .build();
        return concertSeatRepository.save(concertSeat);
    }
//    /**
//     * 특정 콘서트가 예약 가능한 좌석을 보유하고 있는지 확인
//     *
//     * @param concertId 콘서트 ID
//     * @return 예약 가능한 좌석 보유 여부
//     */
//    public boolean hasAvailableSeats(String concertId) {
//        return !concertSeatRepository.findByConcertIdAndUseYn(concertId, UseYn.Y).isEmpty();
//    }
}
