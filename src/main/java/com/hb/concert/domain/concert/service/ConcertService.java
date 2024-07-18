package com.hb.concert.domain.concert.service;

import com.hb.concert.application.concert.command.ConcertCommand;
import com.hb.concert.domain.common.enumerate.ValidState;
import com.hb.concert.domain.concert.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConcertService {

    private final ConcertRepository concertRepository;
    private final ConcertDetailRepository concertDetailRepository;
    private final ConcertSeatRepository concertSeatRepository;

    public ConcertService(ConcertRepository concertRepository, ConcertDetailRepository concertDetailRepository, ConcertSeatRepository concertSeatRepository) {
        this.concertRepository = concertRepository;
        this.concertDetailRepository = concertDetailRepository;
        this.concertSeatRepository = concertSeatRepository;
    }

    /**
     * 특정 콘서트를 ID로 조회
     *
     * @param concertId 콘서트 ID
     * @return 콘서트
     */
    public Concert findByConcertId(String concertId) {
        return concertRepository.findByConcertId(concertId);
    }

    /**
     * 특정 콘서트의 예약 가능한 상세 정보 목록을 조회
     *
     * @param concertId 콘서트 ID
     * @param currentDate 현재 날짜
     * @return 예약 가능한 콘서트 상세 정보 목록
     */
    public List<ConcertDetail> findAvailableDetails(String concertId, LocalDate currentDate) {
        return concertDetailRepository.findByConcertIdAndConcertDateAfterAndValidState(concertId, currentDate, ValidState.VALID);
    }

    /**
     * 현재 날짜 이후 예약 가능한 모든 콘서트 ID 목록을 조회
     *
     * @param currentDate 현재 날짜
     * @return 예약 가능한 콘서트 ID 목록
     */
    public List<Concert> getAvailableConcerts(LocalDate currentDate) {
        List<String> concertIdList = concertDetailRepository.findDistinctConcertIdByConcertDateAfter(currentDate);
        return concertIdList.stream()
                .map(concertRepository::findByConcertId)
                .collect(Collectors.toList());
    }

    /**
     * 특정 콘서트의 예약 가능한 좌석 목록을 조회
     *
     * @param command  콘서트 ID, detailId 일정ID
     * @return 예약 가능한 콘서트 좌석 목록
     */
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

    /**
     * concertId validation
     * @param concertId
     * @return boolean
     */
    public boolean isConcertCountNotFound(String concertId) {
        return concertRepository.countByConcertId(concertId) == 0;
    }

    /**
     * detailId validation
     * @param concertId
     * @param detailId
     * @return boolean
     */
    public boolean isConcertDetailCountNotFound(String concertId, String detailId) {
        return concertDetailRepository.countByConcertIdAndConcertDetailId(concertId, detailId) == 0;
    }
}
