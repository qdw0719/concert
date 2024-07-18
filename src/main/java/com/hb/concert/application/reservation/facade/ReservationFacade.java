package com.hb.concert.application.reservation.facade;

import com.hb.concert.application.concert.command.ConcertCommand;
import com.hb.concert.application.reservation.command.ReservationCommand;
import com.hb.concert.application.reservation.command.ReservationDetailCommand;
import com.hb.concert.domain.concert.service.ConcertService;
import com.hb.concert.domain.reservation.service.ReservationService;
import com.hb.concert.application.history.command.HistoryCreateCommand;
import com.hb.concert.domain.history.service.HistoryService;
import com.hb.concert.domain.queue.service.QueueService;
import com.hb.concert.domain.common.enumerate.UseYn;
import com.hb.concert.domain.common.enumerate.ValidState;
import com.hb.concert.domain.history.History;
import com.hb.concert.domain.reservation.Reservation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
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
        Reservation reservation = reservationService.createReservation(command);

        HistoryCreateCommand.HistoryCreate historyCommand = new HistoryCreateCommand.HistoryCreate(
                reservation.getUserId(), History.HistoryType.RESERVATION, reservation.getReservationTime(), History.HistoryStatus.SUCCESS, null
        );
        historyService.saveHistory(historyCommand);

        List<Integer> concertSeatIdList = command.seatIdList();
        for (Integer seatId : concertSeatIdList) {
            ConcertCommand.saveConcertSeat seatCommand = new ConcertCommand.saveConcertSeat(
                    seatId, reservation.getConcertId(), reservation.getConcertDetailId(), UseYn.N
            );
            concertService.saveConcertSeat(seatCommand);
        }

        ReservationDetailCommand.CreateReservationDetail detailCommand = new ReservationDetailCommand.CreateReservationDetail(reservation.getReservationId(), concertSeatIdList);
        reservationService.createReservationDetails(detailCommand, concertSeatIdList);

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
     *
     * @description 1분 schedule
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
            for (Integer seatId : reservedSeatIdList) {
                ConcertCommand.saveConcertSeat seatCommand = new ConcertCommand.saveConcertSeat(
                        seatId, reservation.getConcertId(), reservation.getConcertDetailId(), UseYn.Y
                );
                concertService.saveConcertSeat(seatCommand);
            }

            reservation.setValidState(ValidState.INVALID);
            reservationService.saveReservation(reservation);
        }
    }
}