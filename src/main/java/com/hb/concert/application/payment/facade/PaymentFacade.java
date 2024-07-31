package com.hb.concert.application.payment.facade;

import com.hb.concert.application.payment.PaymentCommand;
import com.hb.concert.domain.concert.service.ConcertService;
import com.hb.concert.domain.payment.Payment;
import com.hb.concert.domain.payment.service.PaymentService;
import com.hb.concert.domain.queueToken.service.QueueTokenRedisService;
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

    public PaymentFacade(PaymentService paymentService, ConcertService concertService, QueueTokenRedisService queueTokenService, RedissonClient redissonClient) {
        this.paymentService = paymentService;
        this.concertService = concertService;
        this.queueTokenService = queueTokenService;
        this.redissonClient = redissonClient;
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
                    payment = paymentService.processedPayment(reservationId);
                } finally {
                    lock.unlock();
                }
            } else {
                throw new RuntimeException("Could not acquire lock for processing payment");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to acquire lock for processing payment", e);
        }

        concertService.completeReserved(userId, reservationId);
        queueTokenService.expiredTokenAfterPayment(token);

        return payment;
    }
}