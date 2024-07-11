package com.hb.concert.presentation.payment;

import com.hb.concert.domain.payment.Payment;

import java.time.LocalDateTime;

public record PaymentResponse(
        String paymentId,
        String userId,
        Integer amount,
        Integer totalPrice,
        LocalDateTime regTime
) {
    public static PaymentResponse of(Payment payment) {
        return new PaymentResponse(
                payment.getPaymentId(),
                payment.getUserId().toString(),
                payment.getAmount(),
                payment.getTotalPrice(),
                payment.getRegTime()
        );
    }
}
