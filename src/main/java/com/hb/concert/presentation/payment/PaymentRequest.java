package com.hb.concert.presentation.payment;

import com.hb.concert.application.payment.command.PaymentCommand;
import java.util.UUID;

public record PaymentRequest(
        UUID userId,
        Integer amount,
        Integer totalPrice,
        String reservationId,
        String token
) {
    public PaymentCommand.CreatePayment toCommand() {
        return new PaymentCommand.CreatePayment(
                userId, amount, totalPrice, reservationId, token);
    }
}
