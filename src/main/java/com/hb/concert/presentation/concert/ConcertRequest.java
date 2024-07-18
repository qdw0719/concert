package com.hb.concert.presentation.concert;

import com.hb.concert.application.concert.command.ConcertCommand;

import java.time.LocalDate;
import java.util.UUID;

public record ConcertRequest(UUID userId, String concertId, String token) {

    public ConcertCommand.GetAvailableDetails toGetAvailableDetailsCommand() {
        return new ConcertCommand.GetAvailableDetails(userId, concertId, LocalDate.now(), token);
    }
}
