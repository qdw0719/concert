package com.hb.concert.application.payment.facade;

import com.hb.concert.application.concert.command.ConcertCommand;
import com.hb.concert.application.history.command.HistoryCreateCommand;
import com.hb.concert.domain.concert.service.ConcertService;
import com.hb.concert.domain.history.service.HistoryService;
import com.hb.concert.application.payment.command.PaymentCommand;
import com.hb.concert.domain.payment.service.PaymentService;
import com.hb.concert.domain.queue.QueueToken;
import com.hb.concert.domain.queue.service.QueueService;
import com.hb.concert.domain.reservation.service.ReservationService;
import com.hb.concert.domain.exception.CustomException;
import com.hb.concert.domain.exception.CustomException.BadRequestException;
import com.hb.concert.domain.common.enumerate.UseYn;
import com.hb.concert.domain.history.History;
import com.hb.concert.domain.payment.Payment;
import com.hb.concert.domain.reservation.Reservation;
import com.hb.concert.domain.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service @Slf4j
public class PaymentFacade {

    private final PaymentService paymentService;
    private final ReservationService reservationService;
    private final QueueService queueService;
    private final UserService userService;
    private final HistoryService historyService;
    private final ConcertService concertService;

    public PaymentFacade(PaymentService paymentService, ReservationService reservationService, QueueService queueService, UserService userService, HistoryService historyService, ConcertService concertService) {
        this.paymentService = paymentService;
        this.reservationService = reservationService;
        this.queueService = queueService;
        this.userService = userService;
        this.historyService = historyService;
        this.concertService = concertService;
    }

    /**
     * 결제 요청 처리
     *
     * @param command 결제 생성
     * @return PaymentResponse 결제 결과
     */
    @Transactional
    public Payment createPayment(PaymentCommand.CreatePayment command) {
        log.info("Request payment by user: {}", command.userId());

        if (!reservationService.hasReservation(command.reservationId())) {
            log.warn("Reservation not found for id: {}", command.reservationId());
            throw new CustomException.NotFoundException(CustomException.NotFoundException.RESERVATION_NOT_FOUND);
        }

        Reservation reservationInfo = reservationService.getReservationInfoByUser(command.userId());

        if (reservationInfo.getReservationTime().isBefore(reservationInfo.getTemporaryGrantTime())) {
            log.warn("Payment request timeout for user: {}", command.userId());

            queueService.expiredQueue(reservationInfo.getUserId());

            HistoryCreateCommand.HistoryCreate historyCommand = new HistoryCreateCommand.HistoryCreate(
                    command.userId(), History.HistoryType.PAYMENT, LocalDateTime.now(), History.HistoryStatus.FAIL, "결제 요청시간 초과"
            );
            historyService.saveHistory(historyCommand);

            List<Integer> reservedSeatIdList = reservationService.getConcertSeatIdByReservationId(reservationInfo.getReservationId());
            for (Integer seatId : reservedSeatIdList) {
                ConcertCommand.SaveConcertSeat seatCommand = new ConcertCommand.SaveConcertSeat(
                        seatId, reservationInfo.getConcertId(), reservationInfo.getConcertDetailId(), UseYn.Y
                );
                concertService.saveConcertSeat(seatCommand);
            }

            throw new CustomException.BadRequestException(BadRequestException.PAYMENT_REQUEST_TIMEOUT);
        }

        userService.deductBalance(command.userId(), command.totalPrice());

        // 결제 생성
        Payment payment = paymentService.createPayment(command);

        // 예약정보 변경(결제완료)
        Reservation reservation = reservationService.getReservationInfo(reservationInfo.getReservationId());
        reservation.setIsPaid(UseYn.Y);
        reservationService.saveReservation(reservation);

        // 토큰 만료처리
        QueueToken token = queueService.getTokenInfo(command.token());
        token.setStatus(QueueToken.TokenStatus.EXPIRED);
        queueService.saveToken(token);

        // 히스토리 적재
        HistoryCreateCommand.HistoryCreate historyCommand = new HistoryCreateCommand.HistoryCreate(
                payment.getUserId(), History.HistoryType.PAYMENT, payment.getRegTime(), History.HistoryStatus.SUCCESS, null
        );
        historyService.saveHistory(historyCommand);

        log.info("Completed pay by user : {}, price : {}, reservationId: {}", command.userId(), command.totalPrice(), reservationInfo.getReservationId());

        return payment;
    }
}