package com.hb.concert.application.payment;

import java.util.UUID;

public class PaymentCommand {
    public record Process(UUID userId, String reservationId, String token) {}
}
