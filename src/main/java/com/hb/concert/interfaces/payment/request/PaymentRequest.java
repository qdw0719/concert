package com.hb.concert.interfaces.payment.request;

import com.hb.concert.application.payment.PaymentCommand;

import java.util.UUID;

public record PaymentRequest(UUID userId, String reservationId, String token) {
    public PaymentCommand.Process toProcessedCommand() {
        return new PaymentCommand.Process(userId, reservationId, token);
    }
}
