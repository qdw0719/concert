package com.hb.concert.payment.interfaces.response;

import com.hb.concert.payment.entity.Payment;

import java.time.LocalDateTime;

public record PaymentResponse(String reservationId, LocalDateTime paidTime) {
    public static PaymentResponse of(Payment payment) {
        return new PaymentResponse(payment.getReservationId(), payment.getPaidTime());
    }
}
