package com.hb.concert.domain.concert.repository;

import com.hb.concert.domain.concert.Concert;
import com.hb.concert.domain.concert.ConcertDetail;
import com.hb.concert.domain.concert.ConcertReservation;
import com.hb.concert.domain.concert.ConcertSeatConfig;
import com.hb.concert.domain.concert.ViewData.ScheduleInfo;
import com.hb.concert.domain.concert.ViewData.ConcertInfo;
import com.hb.concert.domain.concert.ViewData.SeatInfo;

import java.util.List;
import java.util.Optional;

public interface ConcertRepository {

    List<ConcertInfo> getConcertInfo();

    ScheduleInfo getScheduleInfo(String concertId);

    SeatInfo getSeatInfo(String concertId, String detailId);

    void saveAll(List<Concert> concertList);

    void detailSaveAll(List<ConcertDetail> concertDetailList);

    void seatSaveAll(List<ConcertSeatConfig> concertSeatConfigList);

    Optional<ConcertDetail> getConcertDetailInfo(String concertDetailId);

    void concertDetailSave(ConcertDetail concertDetail);

    long concertCount();
    long detailCount();
    long seatCount();

    void reservationSaveAll(List<ConcertReservation> concertReservations);
}