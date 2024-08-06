package com.hb.concert.domain.concert;

import java.time.LocalDate;
import java.util.List;

public class ViewData {
    public record ConcertInfo(String concertId, String concertDetailId, String concertName, String artist, LocalDate concertDate, Integer availableSeatCount) {}
    public record ScheduleInfo(List<ConcertDetail> scheduleInfo) {}
    public record SeatInfo(List<ConcertSeatConfig> seatInfo) {}
}
