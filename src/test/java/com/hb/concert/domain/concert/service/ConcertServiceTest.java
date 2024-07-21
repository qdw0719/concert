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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ConcertServiceTest {

    @Mock private ConcertRepository concertRepository;
    @Mock private ConcertDetailRepository concertDetailRepository;
    @Mock private ConcertSeatRepository concertSeatRepository;
    @InjectMocks private ConcertService concertService;

    List<String> concertIdList;
    List<Concert> concertList;
    List<ConcertDetail> concertDetailList;
    List<ConcertSeat> concertSeatList;

    @BeforeEach
    void setUp() {
        // given
        MockitoAnnotations.openMocks(this);

        concertIdList = List.of("concert1", "concert2", "concert3");

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
        // when
        when(concertDetailRepository.findDistinctConcertIdByConcertDateAfter(LocalDate.now())).thenReturn(concertIdList);

        List<Concert> result = concertService.getAvailableConcerts(LocalDate.now());

        // then
        assertEquals(3, result.size());
    }

    @Test
    void 예약_가능한_콘서트_일정_조회() {
        // given
        String concertId = "concert1";

        // when
        when(concertDetailRepository.findByConcertIdAndConcertDateAfterAndValidState(concertId, LocalDate.now(), ValidState.VALID)).thenReturn(concertDetailList);

        List<ConcertDetail> result = concertService.findAvailableDetails(concertId, LocalDate.now());

        // then
        assertEquals(3, result.size());
    }

    @Test
    void 예약_가능한_콘서트_정보_없음() {
        // when
        when(concertDetailRepository.findDistinctConcertIdByConcertDateAfter(LocalDate.now())).thenReturn(new ArrayList());

        List<Concert> result = concertService.getAvailableConcerts(LocalDate.now());

        // then
        assertEquals(0, result.size());
    }

    @Test
    void 콘서트_좌석_조회() {
        // given
        UUID userId = UUID.randomUUID();
        String concertId = "concert1";
        String detailId = "detail1";
        String token = "valid_token";

        ConcertCommand.GetConcertSeat command = new ConcertCommand.GetConcertSeat(userId, concertId, detailId, token);

        // when
        when(concertSeatRepository.findByConcertIdAndConcertDetailId(command.concertId(), command.detailId())).thenReturn(concertSeatList);

        List<ConcertSeat> result = concertService.findConcertSeats(command);

        // then
        assertEquals(50, result.size());
    }

    @Test
    void 콘서트_좌석_배정() {
//        // given
//        String concertId = "concert1";
//        String detailId = "detail1";
//
//        ConcertCommand.SaveConcertSeat command = new ConcertCommand.SaveConcertSeat(1, concertId, detailId, UseYn.Y);
//
//        ConcertSeat seat = new ConcertSeat(1L, 1, concertId, detailId, 17500, UseYn.Y);
//
//        // when
//        when(concertSeatRepository.save(any(ConcertSeat.class))).thenReturn(seat);
//
//        ConcertSeat result = concertService.saveConcertSeat(command);
//
//        // then
//        assertEquals(1, result.getConcertSeatId());
//        assertEquals(concertId, result.getConcertId());
//        assertEquals(detailId, result.getConcertDetailId());
//        assertEquals(UseYn.Y, result.getUseYn());
    }

    @Test
    void 콘서트ID_validation() {
        // given
        String concertId = "null";

        // when
        when(concertRepository.countByConcertId(concertId)).thenReturn(0);

        // then
        assertTrue(concertService.isConcertCountNotFound(concertId));
    }
}