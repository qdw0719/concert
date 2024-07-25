package com.hb.concert.application.reservation.facade;

import com.hb.concert.application.reservation.command.ReservationCommand;
import com.hb.concert.application.reservation.command.ReservationDetailCommand;
import com.hb.concert.domain.concert.service.ConcertService;
import com.hb.concert.domain.reservation.service.ReservationService;
import com.hb.concert.application.history.command.HistoryCreateCommand;
import com.hb.concert.domain.history.service.HistoryService;
import com.hb.concert.domain.queue.service.QueueService;
import com.hb.concert.domain.common.enumerate.UseYn;
import com.hb.concert.domain.history.History;
import com.hb.concert.domain.reservation.Reservation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service @Slf4j
public class ReservationFacade {

    private final ReservationService reservationService;
    private final ConcertService concertService;
    private final HistoryService historyService;
    private final QueueService queueService;

    public ReservationFacade(ReservationService reservationService, ConcertService concertService, HistoryService historyService, QueueService queueService) {
        this.reservationService = reservationService;
        this.concertService = concertService;
        this.historyService = historyService;
        this.queueService = queueService;
    }

    /**
     * 예약 요청 처리
     *
     * @param command 예약 생성
     * @return Reservation 예약 내역
     */
    @Transactional
    public ReservationCommand.ResponseReservationInfo createReservation(ReservationCommand.Create command) {
        long startTime = System.currentTimeMillis();

        Reservation reservation;
        List<Integer> concertSeatIdList;

        reservation = reservationService.createReservation(command);

        HistoryCreateCommand.HistoryCreate historyCommand = new HistoryCreateCommand.HistoryCreate(
                reservation.getUserId(), History.HistoryType.RESERVATION, reservation.getReservationTime(), History.HistoryStatus.SUCCESS, null
        );
        historyService.saveHistory(historyCommand);

        concertSeatIdList = command.seatIdList();
        concertService.saveConcertSeat(command.concertId(), command.concertDetailId(), concertSeatIdList);

        ReservationDetailCommand.CreateReservationDetail detailCommand = new ReservationDetailCommand.CreateReservationDetail(reservation.getReservationId(), concertSeatIdList);
        reservationService.createReservationDetails(detailCommand, concertSeatIdList);

        long endTime = System.currentTimeMillis();
        log.info("총 걸린 시간 >>> {} ms", endTime - startTime);

        return new ReservationCommand.ResponseReservationInfo(
                reservation.getReservationId(),
                reservation.getUserId(),
                reservation.getConcertId(),
                reservation.getConcertDetailId(),
                concertSeatIdList.toString(),
                UseYn.N
        );
    }


    /**
     * 스케줄 돌면서 토큰 만료처리 및 대기중인 인원 순번 -1
     * 1분 schedule
     */
    @Transactional
    public void expiredReservations() {
        List<Reservation> expiredReservations = reservationService.getExpiredTargetList();

        for (Reservation reservation : expiredReservations) {
            queueService.expiredQueue(reservation.getUserId());

            HistoryCreateCommand.HistoryCreate historyCommand = new HistoryCreateCommand.HistoryCreate(
                    reservation.getUserId(), History.HistoryType.RESERVATION, LocalDateTime.now(), History.HistoryStatus.FAIL, "미결제 유저"
            );
            historyService.saveHistory(historyCommand);

            List<Integer> reservedSeatIdList = reservationService.getConcertSeatIdByReservationId(reservation.getReservationId());
            concertService.saveConcertSeat(reservation.getConcertId(), reservation.getConcertDetailId(), reservedSeatIdList);

            reservation.expiredReservation();
            reservationService.saveReservation(reservation);
        }
    }
}