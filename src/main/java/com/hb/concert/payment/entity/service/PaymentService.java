package com.hb.concert.payment.entity.service;

import com.hb.concert.exception.CustomException.NotFoundException;
import com.hb.concert.payment.entity.Payment;
import com.hb.concert.payment.entity.repository.PaymentRepository;
import com.hb.concert.support.CommonUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
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
        Payment payment = new Payment();
        payment.createPayment(reservationId);
        paymentRepository.save(payment);
        return payment;
    }

    /**
     * 결제 완료처리 메서드
     * @param reservationId
     * @return
     */
    @Transactional
    public Payment pay(String reservationId) {
        Payment payment = getPaymentInfoByReservation(reservationId);
        if (CommonUtil.isNonNull(payment)) {
            payment.successPayment();
            payment = paymentRepository.save(payment);
        } else {
            throw new NotFoundException(NotFoundException.PAYMENT_INFO_NOT_FOUND);
        }
        return payment;
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
