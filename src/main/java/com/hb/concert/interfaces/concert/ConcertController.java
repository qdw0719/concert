package com.hb.concert.interfaces.concert;

import com.hb.concert.application.concert.facade.ConcertFacade;
import com.hb.concert.interfaces.concert.request.ConcertRequest;
import com.hb.concert.interfaces.concert.response.ConcertResponse.ConcertInfoResponse;
import com.hb.concert.interfaces.concert.response.ConcertResponse.ScheduleInfoResponse;
import com.hb.concert.interfaces.concert.response.ConcertResponse.SeatInfoResponse;
import com.hb.concert.interfaces.concert.response.ConcertResponse.ReservationResponse;
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

    @GetMapping("/schedule")
    public ResponseEntity<ScheduleInfoResponse> getConcertSchedule(@RequestBody ConcertRequest request) {
        return ResponseEntity.ok(
                ScheduleInfoResponse.of(concertFacade.getScheduleInfo(request.toSearchInfoCommand()))
        );
    }

    @GetMapping("/seatInfo")
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
