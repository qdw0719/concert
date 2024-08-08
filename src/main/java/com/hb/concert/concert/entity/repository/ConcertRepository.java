package com.hb.concert.concert.entity.repository;

import com.hb.concert.concert.entity.Concert;
import com.hb.concert.concert.entity.ConcertDetail;
import com.hb.concert.concert.entity.ConcertReservation;
import com.hb.concert.concert.entity.ConcertSeatConfig;
import com.hb.concert.concert.entity.ViewData.ScheduleInfo;
import com.hb.concert.concert.entity.ViewData.ConcertInfo;
import com.hb.concert.concert.entity.ViewData.SeatInfo;

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