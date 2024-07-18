package com.hb.concert.presentation.concert;

import com.hb.concert.domain.concert.ConcertSeat;

public record ConcertSeatResponse(
        Long id,
        Integer concertSeatId,
        String concertId,
        String concertDetailId,
        Integer price,
        String useYn
) {

    public static ConcertSeatResponse of(ConcertSeat concertSeat) {
        return new ConcertSeatResponse(
                concertSeat.getId(),
                concertSeat.getConcertSeatId(),
                concertSeat.getConcertId(),
                concertSeat.getConcertDetailId(),
                concertSeat.getPrice(),
                concertSeat.getUseYn().name()
        );
    }
}
