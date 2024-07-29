package com.hb.concert.application.payment.facade;

import com.hb.concert.application.payment.PaymentCommand;
import com.hb.concert.domain.concert.service.ConcertService;
import com.hb.concert.domain.payment.Payment;
import com.hb.concert.domain.payment.service.PaymentService;
import com.hb.concert.domain.queueToken.service.QueueTokenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PaymentFacade {

    private final PaymentService paymentService;
    private final ConcertService concertService;
    private final QueueTokenService queueTokenService;

    public PaymentFacade(PaymentService paymentService, ConcertService concertService, QueueTokenService queueTokenService) {
        this.paymentService = paymentService;
        this.concertService = concertService;
        this.queueTokenService = queueTokenService;
    }

    @Transactional
    public Payment processedPayment(PaymentCommand.Process processedCommand, String token) {
        UUID userId = processedCommand.userId();
        String reservationId = processedCommand.reservationId();

        Payment payment = paymentService.processedPayment(reservationId);
        concertService.completeReserved(userId, reservationId);
        queueTokenService.expiredTokenAfterPayment(token);
        queueTokenService.waitTokenPositionReduce();

        return payment;
    }
}