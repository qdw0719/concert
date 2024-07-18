package com.hb.concert.presentation.reservation;

import com.hb.concert.application.reservation.command.ReservationCommand;

public record ReservationResponse(
        String reservationId,
        String userId,
        String concertId,
        String concertDetailId,
        String isPaid,
        String seatId
) {
    public static ReservationResponse of(ReservationCommand.ResponseReservationInfo reservationInfo) {
        return new ReservationResponse(
                reservationInfo.reservationId(),
                reservationInfo.userId().toString(),
                reservationInfo.concertId(),
                reservationInfo.concertDetailId(),
                reservationInfo.isPaid().toString(),
                reservationInfo.seatId()
        );
    }
}
