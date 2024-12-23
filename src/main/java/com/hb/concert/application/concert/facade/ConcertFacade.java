package com.hb.concert.application.concert.facade;

import com.hb.concert.application.concert.command.ConcertCommand;
import com.hb.concert.application.concert.service.ConcertDetailService;
import com.hb.concert.application.concert.service.ConcertSeatService;
import com.hb.concert.application.concert.service.ConcertService;
import com.hb.concert.application.queue.service.QueueService;
import com.hb.concert.domain.concert.Concert;
import com.hb.concert.domain.concert.ConcertDetail;
import com.hb.concert.domain.concert.ConcertSeat;
import com.hb.concert.domain.queue.QueueToken;
import com.hb.concert.domain.queue.QueueTokenRepository;
import com.hb.concert.domain.queue.QueueToken.TokenStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ConcertFacade {
    private final ConcertService concertService;
    private final ConcertDetailService concertDetailService;
    private final ConcertSeatService concertSeatService;
    private final QueueTokenRepository queueTokenRepository;
    private final QueueService queueService;

    public ConcertFacade(ConcertService concertService, ConcertDetailService concertDetailService, ConcertSeatService concertSeatService, QueueTokenRepository queueTokenRepository, QueueService queueService) {
        this.concertService = concertService;
        this.concertDetailService = concertDetailService;
        this.concertSeatService = concertSeatService;
        this.queueTokenRepository = queueTokenRepository;
        this.queueService = queueService;
    }

    /**
     * 예약 가능한 콘서트 목록을 조회하는 메소드
     *
     * @return 예약 가능한 콘서트 목록
     */
    public List<Concert> getAvailableConcerts() {
        return concertDetailService.findAvailableConcertIdList(LocalDate.now());
    }

    /**
     * 특정 콘서트의 예약 가능한 상세 정보를 조회하는 메소드
     *
     * @param command GetAvailableDetails
     * @return 예약 가능한 콘서트 상세 정보 목록
     */
    public List<ConcertDetail> getAvailableDetails(ConcertCommand.GetAvailableDetails command) {
        QueueToken token = queueTokenRepository.findByToken(command.token());
        if (token.getStatus() != TokenStatus.WAIT) {
            throw new IllegalArgumentException("대기열에 대기중인 토큰이 아닙니다.");
        }

        // 대기열 순번이 0인지 체크
        if (!queueService.isQueuePositionZero(command.token())) {
            QueueToken queueToken = queueService.getUserToken(command.userId());
            int position = queueToken.getPosition();
            int waitTime = queueToken.getWaitTime();
            throw new IllegalArgumentException(String.format("대기열 순번이 0이 아닙니다. 현재 대기순번: %d, 남은 대기시간: %d " + position, waitTime));
        }

        // 토큰 상태를 PROCESS로 변경
        token.setStatus(TokenStatus.PROCESS);
        queueTokenRepository.save(token);

        return concertDetailService.findAvailableDetails(command.concertId(), command.currentDate());
    }

    /**
     * 특정 콘서트의 예약 가능한 좌석 정보를 조회하는 메소드
     *
     * @param command 콘서트 ID, detailId 일정ID
     * @return 예약 가능한 콘서트 좌석 정보 목록
     */
    public List<ConcertSeat> getConcertSeat(ConcertCommand.getConcertSeat command) {
        return concertSeatService.findConcertSeats(command);
    }
}
