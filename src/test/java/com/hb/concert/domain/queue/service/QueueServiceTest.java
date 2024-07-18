package com.hb.concert.domain.queue.service;

import com.hb.concert.application.queue.command.QueueCommand;
import com.hb.concert.support.config.util.JwtUtil;
import com.hb.concert.domain.common.enumerate.UseYn;
import com.hb.concert.domain.exception.CustomException;
import com.hb.concert.domain.queue.QueueToken;
import com.hb.concert.domain.queue.QueueTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class QueueServiceTest {

    @Mock
    QueueTokenRepository queueTokenRepository;

    @Mock
    JwtUtil jwtUtil;

    @InjectMocks
    QueueService queueService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 토큰발급_대기열_없을_때_0번째_순번() {
        //given
        UUID userId = UUID.randomUUID();
        QueueCommand.Generate command = new QueueCommand.Generate(userId);
        String generatedToken = "generatedToken";

        //when
        when(queueTokenRepository.findByStatus(QueueToken.TokenStatus.WAIT)).thenReturn(new ArrayList<>());
        when(jwtUtil.generateToken(any(UUID.class), anyInt(), anyInt())).thenReturn(generatedToken);
        when(queueTokenRepository.save(any(QueueToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        //then
        QueueToken result = queueService.generateToken(command);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(0, result.getPosition());
        assertEquals(QueueToken.TokenStatus.PROCESS, result.getStatus());
        assertEquals(generatedToken, result.getToken());
    }

    @Test
    void 토큰발급_대기열_존재할_때_n번째() {
        //given
        UUID userId = UUID.randomUUID();
        QueueCommand.Generate command = new QueueCommand.Generate(userId);
        List<QueueToken> queueToken = List.of(
                QueueToken.builder()
                        .id(1L).userId(UUID.randomUUID()).token("testToken").position(1).waitTime(5).isActive(UseYn.N).status(QueueToken.TokenStatus.WAIT).createdTime(LocalDateTime.now())
                        .build()
        );
        List<QueueToken> processedTokenList = new ArrayList<>(Collections.nCopies(50, null));
        String generatedToken = "generatedToken";

        //when
        when(queueTokenRepository.findByStatus(QueueToken.TokenStatus.PROCESS)).thenReturn(processedTokenList);
        when(queueTokenRepository.findByStatus(QueueToken.TokenStatus.WAIT)).thenReturn(queueToken);
        when(jwtUtil.generateToken(any(UUID.class), anyInt(), anyInt())).thenReturn(generatedToken);
        when(queueTokenRepository.save(any(QueueToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        QueueToken result = queueService.generateToken(command);

        // then
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(2, result.getPosition());
        assertEquals(QueueToken.TokenStatus.WAIT, result.getStatus());
        assertEquals(generatedToken, result.getToken());
    }

    @Test
    void 모든_서비스_이용이_정상적으로_끝나고_토큰상태_변경() {
        // given
        UUID userId = UUID.randomUUID();
        String token = "processed_token";
        QueueCommand.TokenCompleted command = new QueueCommand.TokenCompleted(userId, token);

        List<QueueToken> processedTokenList = List.of(
                QueueToken.builder()
                        .id(1L).userId(UUID.randomUUID()).token("first_processed_token").position(0).waitTime(0).isActive(UseYn.Y).status(QueueToken.TokenStatus.PROCESS).createdTime(LocalDateTime.now())
                        .build(),
                QueueToken.builder()
                        .id(2L).userId(userId).token(token).position(0).waitTime(0).isActive(UseYn.Y).status(QueueToken.TokenStatus.PROCESS).createdTime(LocalDateTime.now())
                        .build()
        );

        // when
        when(queueTokenRepository.findByStatus(QueueToken.TokenStatus.PROCESS)).thenReturn(processedTokenList);
        when(queueTokenRepository.save(any(QueueToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        queueService.processCompletedToken(command);

        // then
        InOrder inOrder = inOrder(queueTokenRepository);
        inOrder.verify(queueTokenRepository).findByStatus(QueueToken.TokenStatus.PROCESS);
        inOrder.verify(queueTokenRepository, times(1)).save(any(QueueToken.class));

        assertEquals(QueueToken.TokenStatus.EXPIRED, processedTokenList.get(1).getStatus());
        assertEquals(UseYn.N, processedTokenList.get(1).getIsActive());
    }

    @Test
    void 특정_대기중인_사용자의_토큰을_강제_만료_시키기() {
        // given
        UUID userId = UUID.randomUUID();
        String token = "waiting_token";
        QueueToken queueToken = new QueueToken().builder()
                .id(1L).token(token).position(24).waitTime(120).isActive(UseYn.N).status(QueueToken.TokenStatus.WAIT)
                .build();

        // when
        when(queueTokenRepository.findByUserIdAndStatus(userId, QueueToken.TokenStatus.WAIT)).thenReturn(queueToken);
        when(queueTokenRepository.save(any(QueueToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // then
        verify(queueTokenRepository, times(1));
    }

    @Test
    void 대기열토큰_대기순번_감소시켜주기() {
        // given
        List<QueueToken> waitingTokenList = List.of(
                QueueToken.builder()
                        .id(1L).userId(UUID.randomUUID()).token("first_wait_token").position(11).waitTime(55).isActive(UseYn.N).status(QueueToken.TokenStatus.WAIT).createdTime(LocalDateTime.now())
                        .build(),
                QueueToken.builder()
                        .id(2L).userId(UUID.randomUUID()).token("second_wait_token").position(1).waitTime(5).isActive(UseYn.N).status(QueueToken.TokenStatus.WAIT).createdTime(LocalDateTime.now())
                        .build(),
                QueueToken.builder()
                        .id(3L).userId(UUID.randomUUID()).token("third_wait_token").position(2).waitTime(10).isActive(UseYn.N).status(QueueToken.TokenStatus.WAIT).createdTime(LocalDateTime.now())
                        .build()
        );

        // when
        when(queueTokenRepository.findByStatusOrderByPositionAsc(QueueToken.TokenStatus.WAIT)).thenReturn(waitingTokenList);

        // then
        verify(queueTokenRepository, times(1));
    }

    @Test
    void 대기열_순번_조회_대기열에_없는_토큰일_때() {
        // given
        UUID userId = UUID.randomUUID();
        String token = "expired_token";
        QueueToken queueToken = new QueueToken().builder()
                .id(1L).token(token).position(0).waitTime(0).isActive(UseYn.N).status(QueueToken.TokenStatus.EXPIRED)
                .build();

        // when
        when(queueTokenRepository.findByToken(token)).thenReturn(queueToken);

        // then
        assertThrows(CustomException.QueueException.class, () -> {
            queueService.tokenStatusAndWaitingCheckToProcess(userId, token);
        });
    }

    @Test
    void 대기열_순번_조회_대기_순번이_0이_아닐_때() {
        // given
        UUID userId = UUID.randomUUID();
        String token = "waiting_token";
        QueueToken queueToken = new QueueToken().builder()
                .id(1L).token(token).position(1).waitTime(5).isActive(UseYn.N).status(QueueToken.TokenStatus.WAIT)
                .build();

        // when
        when(queueTokenRepository.findByToken(token)).thenReturn(queueToken);

        // then
        CustomException.QueueException exception = assertThrows(CustomException.QueueException.class, () -> {
            queueService.tokenStatusAndWaitingCheckToProcess(userId, token);
        });
    }
}
