package com.hb.concert.application.queue.service;

import com.hb.concert.application.queue.command.QueueCommand;
import com.hb.concert.config.util.JwtUtil;
import com.hb.concert.domain.common.enumerate.UseYn;
import com.hb.concert.domain.queue.QueueToken;
import com.hb.concert.domain.queue.QueueToken.TokenStatus;
import com.hb.concert.domain.queue.QueueTokenRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class QueueService {

    @Value("${queue.max.size}")
    private int MAX_QUEUE_SIZE;

    private List<QueueToken> tokenList;
    private AtomicInteger nextPosition;

    private final QueueTokenRepository queueTokenRepository;
    private final JwtUtil jwtUtil;

    public QueueService(QueueTokenRepository queueTokenRepository, JwtUtil jwtUtil) {
        this.queueTokenRepository = queueTokenRepository;
        this.jwtUtil = jwtUtil;
    }

    @PostConstruct
    public void init() {
        tokenList = new ArrayList<>();
        nextPosition = new AtomicInteger(1);
    }

    /**
     * 대기열 토큰을 생성하는 메서드
     *
     * @param command 유저의 UUID를 포함한 생성 명령
     * @return 생성된 대기열 토큰
     */
    @Transactional
    public QueueToken generateToken(QueueCommand.Generate command) {
        String token;
        UUID userId = command.userId();
        QueueToken resultQueueToken = new QueueToken();
        if (tokenList.size() < MAX_QUEUE_SIZE) {
            token = jwtUtil.generateToken(userId, 0, 0);
            QueueToken queueToken = QueueToken.builder()
                    .token(token)
                    .userId(userId)
                    .position(0)
                    .waitTime(0)
                    .isActive(UseYn.Y)
                    .status(TokenStatus.PROCESS)
                    .build();
            tokenList.add(queueToken);
            resultQueueToken = queueTokenRepository.save(queueToken);
        } else {
            int position = nextPosition.getAndIncrement();
            int waitTime = calculateWaitTime(position);

            token = jwtUtil.generateToken(userId, position, waitTime);

            QueueToken queueToken = QueueToken.builder()
                    .token(token)
                    .userId(userId)
                    .position(position)
                    .waitTime(waitTime)
                    .isActive(UseYn.N)
                    .status(TokenStatus.WAIT)
                    .build();
            resultQueueToken = queueTokenRepository.save(queueToken);
        }
        return resultQueueToken;
    }

    /**
     * 결제가 완료된 토큰을 처리하고 대기 중인 토큰들의 순서를 갱신하는 메서드
     *
     * @param command 결제가 완료된 사용자의 UUID
     */
    @Transactional
    public void processCompletedToken(QueueCommand.TokenCompleted command) {
        Optional<QueueToken> queueToken = tokenList.stream()
                .filter(token -> token.getUserId().equals(command.userId())
                        && token.getToken().equals(command.token())
                        && token.getStatus() == TokenStatus.PROCESS
                )
                .findFirst();

        if (queueToken.isPresent()) {
            QueueToken token = queueToken.get();
            token.setStatus(TokenStatus.EXPIRED);
            queueTokenRepository.save(token);
            tokenList.remove(token);

            List<QueueToken> waitingTokens = queueTokenRepository.findByStatusOrderByPositionAsc(TokenStatus.WAIT);
            for (QueueToken waitingToken : waitingTokens) {
                int newPosition = waitingToken.getPosition() - 1;
                waitingToken.setPosition(newPosition);
                waitingToken.setWaitTime(calculateWaitTime(newPosition));

                if (newPosition == 0) {
                    waitingToken.setStatus(TokenStatus.PROCESS);
                }
                queueTokenRepository.save(waitingToken);
            }
        }
    }

    /**
     * 대기 순번에 따른 대기 시간을 계산하는 메서드
     *
     * @param position 대기 순번
     * @return 계산된 대기 시간 (분 단위)
     */
    private int calculateWaitTime(int position) {
        return position * 5;
    }

    /**
     * 주어진 토큰의 대기 순번이 0인지 확인하는 메서드
     *
     * @param token JWT 토큰
     * @return 대기 순번이 0이면 true, 그렇지 않으면 false
     */
    public boolean isQueuePositionZero(String token) {
        QueueToken queueToken = queueTokenRepository.findByToken(token);
        return queueToken != null && queueToken.getPosition() == 0;
    }

    /**
     * 모든 대기열 토큰을 조회하는 메서드
     *
     * @return 모든 대기열 토큰의 리스트
     */
    public List<QueueToken> getAllTokens() {
        return queueTokenRepository.findAll();
    }

    /**
     * WAIT 상태의 모든 대기열 토큰을 조회하는 메서드
     *
     * @return WAIT 상태의 대기열 토큰의 리스트
     */
    public List<QueueToken> getAllWaitingTokens() {
        return queueTokenRepository.findByStatus(TokenStatus.WAIT);
    }

    /**
     * 특정 사용자의 대기열 토큰을 조회하는 메서드
     *
     * @param userId 사용자의 UUID
     * @return 사용자의 대기열 토큰
     */
    public QueueToken getUserToken(UUID userId) {
        return queueTokenRepository.findByUserIdAndStatus(userId, TokenStatus.WAIT);
    }

    /**
     * 특정 사용자의 토큰을 강제 만료시키는 메서드
     *
     * @param userId 사용자의 UUID
     */
    public void queueExpired(UUID userId) {
        QueueToken queueToken = getUserToken(userId);
        if (queueToken != null) {
            queueToken.setStatus(TokenStatus.EXPIRED);
            queueToken.setIsActive(UseYn.N);
            queueTokenRepository.save(queueToken);
        }
    }
}