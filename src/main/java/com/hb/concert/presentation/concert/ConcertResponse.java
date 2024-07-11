package com.hb.concert.presentation.concert;

import com.hb.concert.domain.concert.Concert;

public record ConcertResponse(Long id, String concertId, String concertName, String artist) {

    public static ConcertResponse of(Concert concert) {
        return new ConcertResponse(
                concert.getId(),
                concert.getConcertId(),
                concert.getConcertName(),
                concert.getArtist()
        );
    }
}
