package com.hb.concert.application.payment.facade;

import com.hb.concert.application.concert.command.ConcertCommand;
import com.hb.concert.application.concert.service.ConcertSeatService;
import com.hb.concert.application.history.command.HistoryCreateCommand;
import com.hb.concert.application.history.service.HistoryService;
import com.hb.concert.application.payment.command.PaymentCommand;
import com.hb.concert.application.payment.service.PaymentService;
import com.hb.concert.application.queue.service.QueueService;
import com.hb.concert.application.reservation.service.ReservationDetailService;
import com.hb.concert.application.reservation.service.ReservationService;
import com.hb.concert.common.exception.BadRequestException;
import com.hb.concert.domain.common.enumerate.UseYn;
import com.hb.concert.domain.history.History;
import com.hb.concert.domain.payment.Payment;
import com.hb.concert.domain.reservation.Reservation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentFacade {

    private final PaymentService paymentService;
    private final ReservationService reservationService;
    private final QueueService queueService;
    private final HistoryService historyService;
    private final ReservationDetailService reservationDetailService;
    private final ConcertSeatService concertSeatService;

    public PaymentFacade(PaymentService paymentService, ReservationService reservationService, QueueService queueService, HistoryService historyService, ReservationDetailService reservationDetailService, ConcertSeatService concertSeatService) {
        this.paymentService = paymentService;
        this.reservationService = reservationService;
        this.queueService = queueService;
        this.historyService = historyService;
        this.reservationDetailService = reservationDetailService;
        this.concertSeatService = concertSeatService;
    }

    /**
     * 결제 요청 처리
     *
     * @param command 결제 생성
     * @return PaymentResponse 결제 결과
     */
    @Transactional
    public Payment createPayment(PaymentCommand.CreatePayment command) {
        Reservation reservationInfo = reservationService.getReservationInfoByUser(command.userId());

        if (reservationInfo.getReservationTime().isBefore(reservationInfo.getTemporaryGrantTime())) {
            queueService.queueExpired(reservationInfo.getUserId());

            HistoryCreateCommand.HistoryCreate historyCommand = new HistoryCreateCommand.HistoryCreate(
                    command.userId(), History.HistoryType.PAYMENT, LocalDateTime.now(), History.HistoryStatus.FAIL, "결제 요청시간 초과"
            );
            historyService.saveHistory(historyCommand);

            List<Integer> reservedSeatIdList = reservationDetailService.getConcertSeatIdByReservationId(reservationInfo.getReservationId());
            for (Integer seatId : reservedSeatIdList) {
                ConcertCommand.saveConcertSeat seatCommand = new ConcertCommand.saveConcertSeat(
                        seatId, reservationInfo.getConcertId(), reservationInfo.getConcertDetailId(), UseYn.Y
                );
                concertSeatService.saveConcertSeat(seatCommand);
            }

            throw new BadRequestException("결제 요청시간이 초과되었습니다. 처음부터 다시 시도해 주세요.");
        }

        Payment payment = paymentService.createPayment(command);

        HistoryCreateCommand.HistoryCreate historyCommand = new HistoryCreateCommand.HistoryCreate(
                payment.getUserId(), History.HistoryType.PAYMENT, payment.getRegTime(), History.HistoryStatus.SUCCESS, null
        );
        historyService.saveHistory(historyCommand);

        return payment;
    }
}
