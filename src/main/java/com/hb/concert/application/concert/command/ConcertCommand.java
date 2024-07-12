package com.hb.concert.application.concert.command;

import com.hb.concert.domain.common.enumerate.UseYn;

import java.time.LocalDate;
import java.util.UUID;

public class ConcertCommand {
    public record GetAvailableDetails(
            UUID userId,
            String concertId,
            LocalDate currentDate,
            String token
    ) {}

    public record getConcertSeat(
            String concertId,
            String detailId
    ) {}

    public record saveConcertSeat(
            Integer concertSeatId,
            String concertId,
            String concertDetailId,
            UseYn useYn
    ) {}
}
