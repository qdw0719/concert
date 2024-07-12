package com.hb.concert.application.concert.service;

import com.hb.concert.domain.concert.Concert;
import com.hb.concert.domain.concert.ConcertRepository;
import org.springframework.stereotype.Service;

@Service
public class ConcertService {

    private final ConcertRepository concertRepository;

    public ConcertService(ConcertRepository concertRepository) {
        this.concertRepository = concertRepository;
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
}
