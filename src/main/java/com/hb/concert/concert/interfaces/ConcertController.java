package com.hb.concert.concert.interfaces;

import com.hb.concert.concert.application.facade.ConcertFacade;
import com.hb.concert.concert.interfaces.request.ConcertRequest;
import com.hb.concert.concert.interfaces.response.ConcertResponse.ConcertInfoResponse;
import com.hb.concert.concert.interfaces.response.ConcertResponse.ScheduleInfoResponse;
import com.hb.concert.concert.interfaces.response.ConcertResponse.SeatInfoResponse;
import com.hb.concert.concert.interfaces.response.ConcertResponse.ReservationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController @RequestMapping("/api/concert")
public class ConcertController {

    private ConcertFacade concertFacade;

    public ConcertController(ConcertFacade concertFacade) {
        this.concertFacade = concertFacade;
    }

    @GetMapping("/available")
    public ResponseEntity<List<ConcertInfoResponse>> getConcertList() {
        return ResponseEntity.ok(
                concertFacade.getConcertInfo().stream()
                        .map(ConcertInfoResponse::of)
                        .collect(Collectors.toList())
        );
    }

    @PostMapping("/schedule")
    public ResponseEntity<ScheduleInfoResponse> getConcertSchedule(@RequestBody ConcertRequest request) {
        return ResponseEntity.ok(
                ScheduleInfoResponse.of(concertFacade.getScheduleInfo(request.toSearchInfoCommand()))
        );
    }

    @PostMapping("/seatInfo")
    public ResponseEntity<SeatInfoResponse> getConcert(@RequestBody ConcertRequest request) {
        return ResponseEntity.ok(
                SeatInfoResponse.of(concertFacade.getSeatInfo(request.toSearchInfoCommand()))
        );
    }

    @PostMapping("/reservation")
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody ConcertRequest request) {
        return ResponseEntity.ok(
                ReservationResponse.of(concertFacade.createReservation(request.toCreateReservationCommand()))
        );
    }
}
