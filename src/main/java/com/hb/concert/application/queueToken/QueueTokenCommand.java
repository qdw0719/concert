package com.hb.concert.application.queueToken;

import java.util.UUID;

public class QueueTokenCommand {
    public record Create(UUID userId, String concertDetailId) {}
    public record Search(String token) {}
}
