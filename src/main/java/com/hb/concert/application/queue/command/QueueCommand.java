package com.hb.concert.application.queue.command;

import java.util.UUID;

public class QueueCommand {
    public record Generate (UUID userId){}
    public record TokenCompleted (UUID userId, String token){}
}
