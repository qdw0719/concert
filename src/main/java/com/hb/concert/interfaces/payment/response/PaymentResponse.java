package com.hb.concert.interfaces.payment.response;

import com.hb.concert.domain.payment.Payment;

import java.time.LocalDateTime;

public record PaymentResponse(String reservationId, LocalDateTime paidTime) {
    public static PaymentResponse of(Payment payment) {
        return new PaymentResponse(payment.getReservationId(), payment.getPaidTime());
    }
}
