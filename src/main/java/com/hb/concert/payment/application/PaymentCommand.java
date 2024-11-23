package com.hb.concert.payment.application;

import java.util.UUID;

public class PaymentCommand {
    public record Process(UUID userId, String reservationId, String token) {}
}
