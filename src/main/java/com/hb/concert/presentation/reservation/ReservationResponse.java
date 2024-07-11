package com.hb.concert.presentation.reservation;

import com.hb.concert.domain.common.enumerate.UseYn;
import com.hb.concert.domain.reservation.Reservation;

public record ReservationResponse(
        String reservationId,
        String userId,
        String concertId,
        String concertDetailId,
        String paymentId,
        UseYn isPaid
) {
    public static ReservationResponse of(Reservation reservation) {
        return new ReservationResponse(
                reservation.getReservationId(),
                reservation.getUserId().toString(),
                reservation.getConcertId(),
                reservation.getConcertDetailId(),
                reservation.getPaymentId(),
                reservation.getIsPaid()
        );
    }
}
