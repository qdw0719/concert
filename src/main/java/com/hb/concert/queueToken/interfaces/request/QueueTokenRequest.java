package com.hb.concert.queueToken.interfaces.request;

import com.hb.concert.queueToken.application.QueueTokenCommand;

import java.util.UUID;

public record QueueTokenRequest(UUID userId, String concertDetailId, String token) {
    public QueueTokenCommand.Create toCreateCommand() {
        return new QueueTokenCommand.Create(userId, concertDetailId);
    }

    public QueueTokenCommand.Search toSearchCommand() {
        return new QueueTokenCommand.Search(token);
    }
}
