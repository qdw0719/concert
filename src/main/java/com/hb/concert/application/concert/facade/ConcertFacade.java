package com.hb.concert.application.concert.facade;

import com.hb.concert.application.concert.command.ConcertCommand;
import com.hb.concert.domain.exception.CustomException;
import com.hb.concert.domain.concert.service.ConcertService;
import com.hb.concert.domain.queue.service.QueueService;
import com.hb.concert.domain.concert.Concert;
import com.hb.concert.domain.concert.ConcertDetail;
import com.hb.concert.domain.concert.ConcertSeat;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ConcertFacade {
    private final ConcertService concertService;
    private final QueueService queueService;

    public ConcertFacade(ConcertService concertService, QueueService queueService) {
        this.concertService = concertService;
        this.queueService = queueService;
    }

    /**
     * 예약 가능한 콘서트 목록을 조회하는 메소드
     *
     * @return 예약 가능한 콘서트 목록
     */
    public List<Concert> getAvailableConcerts() {
        return concertService.getAvailableConcerts(LocalDate.now());
    }

    /**
     * 특정 콘서트의 예약 가능한 상세 정보를 조회하는 메소드
     *
     * @param command GetAvailableDetails
     * @return 예약 가능한 콘서트 상세 정보 목록
     */
    public List<ConcertDetail> getAvailableDetails(ConcertCommand.GetAvailableDetails command) {
        validationConcert(command.concertId(), null);

        queueService.tokenStatusAndWaitingCheckToProcess(command.userId(), command.token());

        return concertService.findAvailableDetails(command.concertId(), command.currentDate());
    }

    /**
     * 특정 콘서트의 예약 가능한 좌석 정보를 조회하는 메소드
     *
     * @param command 콘서트 ID, detailId 일정ID
     * @return 예약 가능한 콘서트 좌석 정보 목록
     */
    @Transactional
    public List<ConcertSeat> getConcertSeat(ConcertCommand.getConcertSeat command) {
        validationConcert(command.concertId(), command.detailId());

        queueService.tokenStatusAndWaitingCheckToProcess(command.userId(), command.token());

        return concertService.findConcertSeats(command);
    }

    /**
     * 콘서트 id와 콘서트 일정id에 대한 validation
     * @param concertId
     * @param detailId
     */
    public void validationConcert(String concertId, String detailId) {
        if (concertService.isConcertCountNotFound(concertId)) {
            throw new CustomException.NotFoundException(CustomException.NotFoundException.CONCERT_NOT_FOUND);
        }

        if (detailId != null && concertService.isConcertDetailCountNotFound(concertId, detailId)) {
            throw new CustomException.NotFoundException(CustomException.NotFoundException.CONCERT_NOT_FOUND);
        }
    }
}
