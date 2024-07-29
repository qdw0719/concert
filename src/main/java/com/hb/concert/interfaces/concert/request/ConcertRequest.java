package com.hb.concert.interfaces.concert.request;

import com.hb.concert.application.concert.ConcertCommand;

import java.util.List;
import java.util.UUID;

public record ConcertRequest(UUID userId, String concertId, String concertDetailId, List<Integer> seatId) {
    public ConcertCommand.Search toSearchInfoCommand() {
        return new ConcertCommand.Search(concertId, concertDetailId);
    }

    public ConcertCommand.CreateReservation toCreateReservationCommand() {
        return new ConcertCommand.CreateReservation(userId, concertDetailId, seatId);
    }
}
