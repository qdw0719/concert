package com.hb.concert.application.reservation.command;

import java.util.List;

public class ReservationDetailCommand {

    public record CreateReservationDetail(
            String reservationId,
            List<Integer> concertSeatId
    ) {}
}
