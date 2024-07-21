package com.hb.concert.presentation.concert;

import com.hb.concert.application.concert.facade.ConcertFacade;
import com.hb.concert.domain.common.enumerate.UseYn;
import com.hb.concert.domain.concert.service.ConcertService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController @RequestMapping("/api/concerts")
public class ConcertController {

    private final ConcertFacade concertFacade;
    private final ConcertService concertService;

    public ConcertController(ConcertFacade concertFacade, ConcertService concertService) {
        this.concertFacade = concertFacade;
        this.concertService = concertService;
    }

    /**
     * 모든 콘서트 목록을 조회
     *
     * @return 모든 콘서트 목록
     */
    @GetMapping
    public ResponseEntity<List<ConcertResponse>> getAvailableConcerts() {
        List<ConcertResponse> response = concertService.getAvailableConcerts(LocalDate.now())
                .stream()
                .map(ConcertResponse::of)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * 특정 콘서트의 상세 정보를 조회
     *
     * @param concertId 콘서트 ID
     * @return 콘서트 상세 정보 목록
     * 
     * @description 
     *      콘서트를 선택했을 때 해당 요청 발생
     */
    @GetMapping("/{concertId}/details") //@TokenValidation
    public ResponseEntity<List<ConcertDetailResponse>> getAvailableDetails(@PathVariable String concertId, HttpServletRequest request) {
        ConcertRequest _concertRequest = (ConcertRequest) request.getAttribute("concertRequest");
        ConcertRequest concertRequest = new ConcertRequest(_concertRequest.userId(), concertId, _concertRequest.token());
        List<ConcertDetailResponse> getAvailableDetails = concertFacade.getAvailableDetails(concertRequest.toGetAvailableDetailsCommand())
                .stream()
                .map(ConcertDetailResponse::of)
                .collect(Collectors.toList());
        return ResponseEntity.ok(getAvailableDetails);
    }

    /**
     * 특정 콘서트의 좌석 정보를 조회
     *
     * @param concertId 콘서트 ID
     * @return 콘서트 좌석 정보 목록
     * 
     * @description
     *      콘서트 일정을 선택했을 때 해당요청 발생
     */
    @GetMapping("/{concertId}/details/{detailId}/seats") //@TokenValidation
    public ResponseEntity<Map<String, List<ConcertSeatResponse>>> getConcertSeats(@PathVariable String concertId, @PathVariable String detailId, HttpServletRequest request) {
        ConcertSeatRequest _concertSeatRequest = (ConcertSeatRequest) request.getAttribute("concertSeatRequest");
        ConcertSeatRequest concertSeatRequest = new ConcertSeatRequest(_concertSeatRequest.userId(), concertId, detailId, _concertSeatRequest.token());
        Map<String, List<ConcertSeatResponse>> response = concertFacade.getConcertSeat(concertSeatRequest.toGetConcertSeatCommand()).stream()
                .collect(Collectors.groupingBy(
                        seat -> seat.getUseYn() == UseYn.Y ? "active" : "non-active",
                        Collectors.mapping(ConcertSeatResponse::of, Collectors.toList())
                ));
        return ResponseEntity.ok(response);
    }
}
