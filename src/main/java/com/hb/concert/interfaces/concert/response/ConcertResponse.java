package com.hb.concert.interfaces.concert.response;

import com.hb.concert.domain.concert.ConcertReservation;
import com.hb.concert.domain.concert.ViewData.ConcertInfo;
import com.hb.concert.domain.concert.ViewData.ScheduleInfo;
import com.hb.concert.domain.concert.ViewData.SeatInfo;

import java.time.LocalDate;

public class ConcertResponse {
    public record ConcertInfoResponse(String concertName, String artist, LocalDate concertDate, Integer availableSeatCount) {
        public static ConcertInfoResponse of(ConcertInfo concertInfo) {
            return new ConcertInfoResponse(concertInfo.concertName(), concertInfo.artist(), concertInfo.concertDate(), concertInfo.availableSeatCount());
        }
    }
    public record ScheduleInfoResponse(ScheduleInfo scheduleInfo) {
        public static ScheduleInfoResponse of(ScheduleInfo scheduleInfo) {
            return new ScheduleInfoResponse(scheduleInfo);
        }
    }
    public record SeatInfoResponse(SeatInfo seatInfo) {
        public static SeatInfoResponse of(SeatInfo seatInfo) {
            return new SeatInfoResponse(seatInfo);
        }
    }
    public record ReservationResponse(ConcertReservation reservationInfo) {
        public static ReservationResponse of(ConcertReservation reservationInfo) {
            return new ReservationResponse(reservationInfo);
        }
    }
}
