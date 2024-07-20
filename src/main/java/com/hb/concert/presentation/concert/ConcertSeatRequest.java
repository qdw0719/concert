package com.hb.concert.presentation.concert;

import com.hb.concert.application.concert.command.ConcertCommand;

import java.util.UUID;

public record ConcertSeatRequest(
        UUID userId,
        String concertId,
        String detailId,
        String token
) {

    public ConcertCommand.GetConcertSeat toGetConcertSeatCommand() {
        return new ConcertCommand.GetConcertSeat(userId, concertId, detailId, token);
    }
}
