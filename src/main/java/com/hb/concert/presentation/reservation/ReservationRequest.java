package com.hb.concert.presentation.reservation;

import com.hb.concert.application.reservation.command.ReservationCommand;

import java.util.List;
import java.util.UUID;

public record ReservationRequest(
        UUID userId,
        String concertId,
        String concertDetailId,
        List<Integer> seatIdList
) {
    ReservationCommand.Create toCreateCommand() {
        return new ReservationCommand.Create(userId, concertId, concertDetailId, seatIdList);
    }
}

