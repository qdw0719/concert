package com.hb.concert.presentation.concert;

import com.hb.concert.application.concert.command.ConcertCommand;

import java.time.LocalDate;
import java.util.UUID;

public record ConcertRequest(String concertId, String token) {
    private static UUID userId;

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getUserId() {
        return userId;
    }

    public ConcertCommand.GetAvailableDetails toGetAvailableDetailsCommand() {
        return new ConcertCommand.GetAvailableDetails(userId, concertId, LocalDate.now(), token);
    }
}
