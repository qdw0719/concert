package com.hb.concert.presentation.queue;

import com.hb.concert.application.queue.command.QueueCommand;

import java.util.UUID;

public record QueueTokenRequest(
        UUID userId,
        String token
) {
    QueueCommand.Generate toGenerateCommand() {
        return new QueueCommand.Generate(userId);
    }

    QueueCommand.TokenCompleted toTokenCompleted() {
        return new QueueCommand.TokenCompleted(userId, token);
    }
}