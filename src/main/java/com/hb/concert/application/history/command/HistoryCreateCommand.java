package com.hb.concert.application.history.command;

import com.hb.concert.domain.history.History;

import java.time.LocalDateTime;
import java.util.UUID;

public class HistoryCreateCommand {

    public record HistoryCreate(
            UUID userId,
            History.HistoryType type,
            LocalDateTime regDate,
            History.HistoryStatus status,
            String failReason
    ) {}
}
