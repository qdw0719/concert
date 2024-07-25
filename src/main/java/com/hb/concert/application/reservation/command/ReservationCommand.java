package com.hb.concert.application.reservation.command;

import com.hb.concert.domain.common.enumerate.UseYn;

import java.util.List;
import java.util.UUID;

public class ReservationCommand {

    public record Create(
            UUID userId,
            String concertId,
            String concertDetailId,
            List<Integer> seatIdList
    ) {}

    public record ResponseReservationInfo(
            String reservationId,
            UUID userId,
            String concertId,
            String concertDetailId,
            String seatId,
            UseYn isPaid
    ) {}

    public record GetReservationInfo(
            String reservationId,
            UUID userId,
            String concertId,
            String concertDetailId,
            List<Integer> seatId
    ) {}
}