package com.hb.concert.presentation.concert;

import com.hb.concert.application.concert.command.ConcertCommand;

public record ConcertSeatRequest(
        String concertId,
        String detailId
) {

    public ConcertCommand.getConcertSeat toGetConcertSeatCommand() {
        return new ConcertCommand.getConcertSeat(concertId, detailId);
    }
}
