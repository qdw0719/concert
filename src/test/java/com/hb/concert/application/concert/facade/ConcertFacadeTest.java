package com.hb.concert.application.concert.facade;

import com.hb.concert.application.concert.command.ConcertCommand;
import com.hb.concert.domain.exception.CustomException;
import com.hb.concert.domain.common.enumerate.UseYn;
import com.hb.concert.domain.concert.ConcertSeat;
import com.hb.concert.domain.queue.QueueToken;
import com.hb.concert.domain.queue.QueueTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ConcertFacadeTest {

    @Autowired private ConcertFacade concertFacade;
    @Autowired private QueueTokenRepository queueTokenRepository;

    @BeforeEach
    void setUp() {
        // 초기 데이터 DataInitialize.class 로 대체
    }

    @Test
    void 콘서트_예약_조회_토큰이_대기중이_아닐_때() {
        // given
        String concertId = "concert1";
        String token = "대기중이아닌토큰";
        UUID userid = UUID.randomUUID();
        LocalDate currentDate = LocalDate.now();

        ConcertCommand.GetAvailableDetails command = new ConcertCommand.GetAvailableDetails(userid, concertId, currentDate, token);

        QueueToken queueToken = new QueueToken();
        queueToken.setUserId(userid);
        queueToken.setIsActive(UseYn.Y);
        queueToken.setToken(token);
        queueToken.setStatus(QueueToken.TokenStatus.PROCESS);
        queueTokenRepository.save(queueToken);

        // when then
        assertThrows(CustomException.QueueException.class, () -> {
           concertFacade.getAvailableDetails(command);
        });
    }

    @Test
    void 콘서트_예약_조회_대기순번이_0이_아닐_때() {
        // given
        String concertId = "concert1";
        String token = "exampleValidToken";
        UUID userid = UUID.randomUUID();
        LocalDate currentDate = LocalDate.now();

        ConcertCommand.GetAvailableDetails command = new ConcertCommand.GetAvailableDetails(userid, concertId, currentDate, token);

        QueueToken queueToken = new QueueToken();
        queueToken.setUserId(userid);
        queueToken.setIsActive(UseYn.N);
        queueToken.setToken(token);
        queueToken.setStatus(QueueToken.TokenStatus.WAIT);
        queueToken.setPosition(5);
        queueToken.setWaitTime(25);
        queueTokenRepository.save(queueToken);

        // when then
        assertThrows(CustomException.QueueException.class, () -> {
           concertFacade.getAvailableDetails(command);
        });
    }

    @Test
    void 콘서트ID_잘못_주어졌을_때() {
        // given
        UUID userId = UUID.randomUUID();
        String concertId = "잘못된콘서트id";
        String detailId = "detail1";
        String token = "valid_token";

        ConcertCommand.GetConcertSeat command = new ConcertCommand.GetConcertSeat(userId, concertId, detailId, token);

        // when then
        assertThrows(CustomException.NotFoundException.class, () -> {
           concertFacade.getConcertSeat(command);
        });
    }

    @Test
    void 콘서트_상세id_잘못_주어졌을_때(){
        // given
        UUID userId = UUID.randomUUID();
        String concertId = "concert1";
        String detailId = "잘못된상세id";
        String token = "valid_token";

        ConcertCommand.GetConcertSeat command = new ConcertCommand.GetConcertSeat(userId, concertId, detailId, token);

        // when then
        assertThrows(CustomException.NotFoundException.class, () -> {
            concertFacade.getConcertSeat(command);
        });
    }

    @Test
    void 콘서트_좌석_조회() {
        // given
        UUID userId = UUID.randomUUID();
        String concertId = "concert1";
        String detailId = "detail1";
        String token = "Bearer TestToken1";

        ConcertCommand.GetConcertSeat command = new ConcertCommand.GetConcertSeat(userId, concertId, detailId, token);
        List<ConcertSeat> result = concertFacade.getConcertSeat(command);

        // when then
        assertNotNull(result);
        assertEquals(50, result.size());
        assertEquals(concertId, result.get(0).getConcertId());
        assertEquals(detailId, result.get(0).getConcertDetailId());
    }
}