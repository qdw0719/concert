package com.hb.concert.payment.interfaces.request;

import com.hb.concert.payment.application.PaymentCommand;

import java.util.UUID;

public record PaymentRequest(UUID userId, String reservationId, String token) {
    public PaymentCommand.Process toProcessedCommand() {
        return new PaymentCommand.Process(userId, reservationId, token);
    }
}
