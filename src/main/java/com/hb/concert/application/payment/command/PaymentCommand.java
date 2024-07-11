package com.hb.concert.application.payment.command;

import java.util.UUID;

public class PaymentCommand {

    public record CreatePayment(
            UUID userId,
            Integer amount,
            Integer totalPrice,
            String reservationId,
            String token
    ) { }
}
