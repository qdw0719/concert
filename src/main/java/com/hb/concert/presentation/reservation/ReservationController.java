package com.hb.concert.presentation.reservation;

import com.hb.concert.application.reservation.facade.ReservationFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationFacade reservationFacade;

    public ReservationController(ReservationFacade reservationFacade) {
        this.reservationFacade = reservationFacade;
    }

    /**
     * 예약 생성 API
     *
     * @param @RequestBody ReservationRequest
     * @return ResponseEntity<ReservationResponse>
     */
    @PostMapping// @TokenValidation
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody ReservationRequest request) {
        return ResponseEntity.ok(
                ReservationResponse.of(reservationFacade.createReservation(request.toCreateCommand()))
        );
    }
}
