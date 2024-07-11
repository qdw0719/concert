package com.hb.concert.application.reservation.command;

import java.util.List;
import java.util.UUID;

public class ReservationCommand {

    public record Create(
            UUID userId,
            String concertId,
            String concertDetailId,
            List<Integer> seatIdList
    ) {}
}
