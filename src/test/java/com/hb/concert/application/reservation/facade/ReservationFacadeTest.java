package com.hb.concert.application.reservation.facade;

import com.hb.concert.application.reservation.command.ReservationCommand;
import com.hb.concert.domain.common.enumerate.UseYn;
import com.hb.concert.domain.common.enumerate.ValidState;
import com.hb.concert.domain.history.History;
import com.hb.concert.domain.history.HistoryRepository;
import com.hb.concert.domain.reservation.Reservation;
import com.hb.concert.domain.reservation.ReservationDetail;
import com.hb.concert.domain.reservation.ReservationDetailRepository;
import com.hb.concert.domain.reservation.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest @Transactional
class ReservationFacadeTest {

    @Autowired private ReservationFacade reservationFacade;

    @Autowired private ReservationRepository reservationRepository;

    @Autowired private ReservationDetailRepository reservationDetailRepository;

    @Autowired private HistoryRepository historyRepository;

    private ReservationCommand.Create reservationCreateCommand;

    @BeforeEach
    public void setUp() {
        // given
        UUID userId = UUID.randomUUID();
        String concertId = "concert1";
        String concertDetailId = "detail1";
        List<Integer> seatIdList = List.of(1, 2, 3);

        reservationCreateCommand = new ReservationCommand.Create(userId, concertId, concertDetailId, seatIdList);
    }

    @Test
    public void 예약테스트() {
        // when
        ReservationCommand.ResponseReservationInfo response = reservationFacade.createReservation(reservationCreateCommand);

        // then

        // response 정상확인
        assertNotNull(response);
        assertEquals(reservationCreateCommand.userId(), response.userId());
        assertEquals(reservationCreateCommand.concertId(), response.concertId());
        assertEquals(reservationCreateCommand.concertDetailId(), response.concertDetailId());

        // 예약
        Reservation reservation = reservationRepository.findByReservationId(response.reservationId());
        assertNotNull(reservation);
        assertEquals(reservation.getReservationId(), response.reservationId());
        assertEquals(UseYn.N, reservation.getIsPaid());
        assertEquals(ValidState.INVALID, reservation.getValidState());

        // 좌석 확정
        List<ReservationDetail> reservationDetails = reservationDetailRepository.findByReservationId(reservation.getReservationId());
        assertEquals(3, reservationDetails.size());

        // 히스토리 적재
        List<History> getUserHistory = historyRepository.findByUserId(reservation.getUserId());
        assertEquals(1, getUserHistory.size());
        assertEquals(History.HistoryStatus.SUCCESS, getUserHistory.get(0).getStatus());
        assertNull(getUserHistory.get(0).getFailReason());
    }
}