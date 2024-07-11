package com.hb.concert.application.concert.service;

import com.hb.concert.domain.concert.Concert;
import com.hb.concert.domain.concert.ConcertDetail;
import com.hb.concert.domain.concert.ConcertDetailRepository;
import com.hb.concert.domain.common.enumerate.ValidState;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ConcertDetailService {

    private final ConcertDetailRepository concertDetailRepository;

    public ConcertDetailService(ConcertDetailRepository concertDetailRepository) {
        this.concertDetailRepository = concertDetailRepository;
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
    public List<Concert> findAvailableConcertIdList(LocalDate currentDate) {
        return concertDetailRepository.findDistinctConcertIdByConcertDateAfter(currentDate);
    }
}
