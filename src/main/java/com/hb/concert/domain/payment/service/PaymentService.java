package com.hb.concert.domain.payment.service;

import com.hb.concert.application.payment.command.PaymentCommand;
import com.hb.concert.domain.payment.Payment;
import com.hb.concert.domain.payment.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service @Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    /**
     * 결제 생성
     *
     * @param command 결제 생성
     * @return Payment 결제
     */
    @Transactional
    public Payment createPayment(PaymentCommand.CreatePayment command) {
        log.info("Request payment user : {}", command.userId());

        Payment payment = Payment.builder()
                .paymentId(UUID.randomUUID().toString())
                .userId(command.userId())
                .amount(command.amount())
                .totalPrice(command.totalPrice())
                .regTime(LocalDateTime.now())
                .build();
        paymentRepository.save(payment);

        return payment;
    }
}