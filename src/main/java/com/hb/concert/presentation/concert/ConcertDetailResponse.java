package com.hb.concert.presentation.concert;

import com.hb.concert.domain.concert.ConcertDetail;

import java.time.LocalDate;

public record ConcertDetailResponse(Long id, String concertDetailId, String concertId, LocalDate concertDate, String location) {

    public static ConcertDetailResponse of(ConcertDetail concertDetail) {
        return new ConcertDetailResponse(
                concertDetail.getId(),
                concertDetail.getConcertDetailId(),
                concertDetail.getConcertId(),
                concertDetail.getConcertDate(),
                concertDetail.getLocation()
        );
    }
}
