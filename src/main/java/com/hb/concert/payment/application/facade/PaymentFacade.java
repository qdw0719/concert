package com.hb.concert.payment.application.facade;

import com.hb.concert.payment.application.PaymentCommand;
import com.hb.concert.concert.entity.service.ConcertService;
import com.hb.concert.payment.entity.Payment;
import com.hb.concert.payment.dataplatform.PaymentCompleteEvent;
import com.hb.concert.payment.entity.pulisher.PaymentEventPublisher;
import com.hb.concert.payment.entity.service.PaymentService;
import com.hb.concert.queueToken.entity.service.QueueTokenRedisService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class PaymentFacade {

    private final PaymentService paymentService;
    private final ConcertService concertService;
    private final QueueTokenRedisService queueTokenService;
    private final RedissonClient redissonClient;
    private final PaymentEventPublisher paymentEventPublisher;

    public PaymentFacade(PaymentService paymentService, ConcertService concertService, QueueTokenRedisService queueTokenService, RedissonClient redissonClient, PaymentEventPublisher paymentEventPublisher) {
        this.paymentService = paymentService;
        this.concertService = concertService;
        this.queueTokenService = queueTokenService;
        this.redissonClient = redissonClient;
        this.paymentEventPublisher = paymentEventPublisher;
    }

    @Transactional
    public Payment processedPayment(PaymentCommand.Process command, String token) {
        UUID userId = command.userId();
        String reservationId = command.reservationId();

        Payment payment;
        RLock lock = redissonClient.getLock("paymentLock:" + reservationId);
        try {
            if (lock.tryLock(10, 10, TimeUnit.SECONDS)) {
                try {
                    payment = paymentService.pay(reservationId);
                } finally {
                    lock.unlock();
                }
            } else {
                throw new RuntimeException("결제에 대한 락을 획득하지 못했습니다.");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("결제에 대한 락을 획득하던 도중 서비스가 중단되었습니다.", e);
        }

        concertService.completeReserved(userId, reservationId);
        queueTokenService.expiredTokenAfterPayment(token);

        PaymentCompleteEvent event = new PaymentCompleteEvent(reservationId);
        paymentEventPublisher.complete(event);

        return payment;
    }
}