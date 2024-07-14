package com.hb.concert.domain.concert.service;

import com.hb.concert.application.concert.command.ConcertCommand;
import com.hb.concert.domain.common.enumerate.UseYn;
import com.hb.concert.domain.common.enumerate.ValidState;
import com.hb.concert.domain.concert.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ConcertServiceTest {

    @Mock
    private ConcertDetailRepository concertDetailRepository;

    @Mock
    private ConcertSeatRepository concertSeatRepository;

    @InjectMocks
    private ConcertService concertService;

    List<Concert> concertList;
    List<ConcertDetail> concertDetailList;
    List<ConcertSeat> concertSeatList;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        concertList = List.of(
                new Concert(1L, "concert1", "김일성 콘서트", "김일성"),
                new Concert(2L, "concert2", "김정일 콘서트", "김정일"),
                new Concert(3L, "concert3", "김정은 콘서트", "김정은")
        );

        concertDetailList = List.of(
          new ConcertDetail(1L, "detail1", "concert1", LocalDate.now(), "평양", ValidState.VALID),
          new ConcertDetail(2L, "detail2", "concert2", LocalDate.now().plusDays(1), "평양", ValidState.VALID),
          new ConcertDetail(3L, "detail3", "concert3", LocalDate.now().plusDays(2), "평양", ValidState.VALID)
        );

        concertSeatList = new ArrayList();
        for (int i  = 0; i < 50; i++) {
            concertSeatList.add(new ConcertSeat(1L, i+1, "concert1", "detail1", 17500, UseYn.Y));
        }
    }

    @Test
    void 예약_가능한_콘서트_조회() {

        when(concertDetailRepository.findDistinctConcertIdByConcertDateAfter(LocalDate.now())).thenReturn(concertList);

        List<Concert> result = concertService.getAvailableConcerts(LocalDate.now());

        assertEquals(3, result.size());
    }

    @Test
    void 예약_가능한_콘서트_일정_조회() {
        String concertId = "concert1";

        when(concertDetailRepository.findByConcertIdAndConcertDateAfterAndValidState(concertId, LocalDate.now(), ValidState.VALID)).thenReturn(concertDetailList);

        List<ConcertDetail> result = concertService.findAvailableDetails(concertId, LocalDate.now());

        assertEquals(3, result.size());
    }

    @Test
    void 예약_가능한_콘서트_정보_없음() {
        when(concertDetailRepository.findDistinctConcertIdByConcertDateAfter(LocalDate.now())).thenReturn(new ArrayList());

        List<Concert> result = concertService.getAvailableConcerts(LocalDate.now());

        assertEquals(0, result.size());
    }

    @Test
    void 콘서트_좌석_조회() {

        String concertId = "concert1";
        String detailId = "detail1";

        ConcertCommand.getConcertSeat command = new ConcertCommand.getConcertSeat(concertId, detailId);

        when(concertSeatRepository.findByConcertIdAndConcertDetailId(command.concertId(), command.detailId())).thenReturn(concertSeatList);

        List<ConcertSeat> result = concertService.findConcertSeats(command);

        assertEquals(50, result.size());
    }
}