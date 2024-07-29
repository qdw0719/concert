package com.hb.concert.domain.payment.service;

import com.hb.concert.domain.exception.CustomException.NotFoundException;
import com.hb.concert.domain.payment.Payment;
import com.hb.concert.domain.payment.repository.PaymentRepository;
import com.hb.concert.support.CommonUtil;
import org.redisson.api.RLock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.redisson.api.RedissonClient;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RedissonClient redissonClient;

    public PaymentService(PaymentRepository paymentRepository, RedissonClient redissonClient) {
        this.paymentRepository = paymentRepository;
        this.redissonClient = redissonClient;
    }

    public Payment getPaymentInfoByReservation(String reservationId) {
        return paymentRepository.getPaymetInfoByReservationId(reservationId).orElseGet(null);
    }

    /**
     * 결제 정보 생성 메서드
     * 예약(좌석 임시배정) 시 생성
     * @param reservationId
     * @return
     */
    @Transactional
    public Payment createPayment(String reservationId) {
        RLock lock = redissonClient.getLock("paymentLock:" + reservationId);
        try {
            if (lock.tryLock(10, 10, TimeUnit.SECONDS)) {
                try {
                    Payment payment = new Payment();
                    payment.createPayment(reservationId);
                    paymentRepository.save(payment);
                    return payment;
                } finally {
                    lock.unlock();
                }
            } else {
                throw new RuntimeException("Could not acquire lock for creating payment");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to acquire lock for creating payment", e);
        }
    }

    /**
     * 결제 완료처리 메서드
     * @param reservationId
     * @return
     */
    @Transactional
    public Payment processedPayment(String reservationId) {
        RLock lock = redissonClient.getLock("paymentLock:" + reservationId);
        try {
            if (lock.tryLock(10, 10, TimeUnit.SECONDS)) {
                try {
                    Payment payment = getPaymentInfoByReservation(reservationId);
                    if (CommonUtil.isNonNull(payment)) {
                        payment.successPayment();
                        payment = paymentRepository.save(payment);
                    } else {
                        throw new NotFoundException(NotFoundException.PAYMENT_INFO_NOT_FOUND);
                    }
                    return payment;
                } finally {
                    lock.unlock();
                }
            } else {
                throw new RuntimeException("Could not acquire lock for processing payment");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to acquire lock for processing payment", e);
        }
    }

    public List<String> getEffectiveTimeAfterNow(String reservationId, LocalDateTime reservationTime) {
        List<String> expiredTargetList = new ArrayList<>();
        Payment paymentInfo = getPaymentInfoByReservation(reservationId);
        if (CommonUtil.isNull(paymentInfo) && reservationTime.isAfter(paymentInfo.getEffectiveTime())) {
            expiredTargetList.add(reservationId);
        }
        return expiredTargetList;
    }
}
