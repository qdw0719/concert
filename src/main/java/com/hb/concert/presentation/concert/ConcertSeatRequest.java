package com.hb.concert.presentation.concert;

import com.hb.concert.application.concert.command.ConcertCommand;

import java.util.UUID;

public record ConcertSeatRequest(
        UUID userId,
        String concertId,
        String detailId,
        String token
) {

    public ConcertCommand.getConcertSeat toGetConcertSeatCommand() {
        return new ConcertCommand.getConcertSeat(userId, concertId, detailId, token);
    }
}
