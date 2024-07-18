package com.hb.concert.domain.queue.service;

import com.hb.concert.application.queue.command.QueueCommand;
import com.hb.concert.support.config.util.JwtUtil;
import com.hb.concert.domain.common.enumerate.UseYn;
import com.hb.concert.domain.exception.CustomException;
import com.hb.concert.domain.queue.QueueToken;
import com.hb.concert.domain.queue.QueueToken.TokenStatus;
import com.hb.concert.domain.queue.QueueTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class QueueService {

//    @Value("${queue.max.size}")
    private int MAX_QUEUE_SIZE = 50;

    private final QueueTokenRepository queueTokenRepository;
    private final JwtUtil jwtUtil;

    public QueueService(QueueTokenRepository queueTokenRepository, JwtUtil jwtUtil) {
        this.queueTokenRepository = queueTokenRepository;
        this.jwtUtil = jwtUtil;
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

        List<QueueToken> processedTokenList = queueTokenRepository.findByStatus(TokenStatus.PROCESS);
        List<QueueToken> waitTokenList = queueTokenRepository.findByStatus(TokenStatus.WAIT);

        QueueToken resultQueueToken = new QueueToken();
        if (processedTokenList.size() < MAX_QUEUE_SIZE) {
            token = jwtUtil.generateToken(userId, 0, 0);
            QueueToken queueToken = QueueToken.builder()
                    .token(token)
                    .userId(userId)
                    .position(0)
                    .waitTime(0)
                    .isActive(UseYn.Y)
                    .status(TokenStatus.PROCESS)
                    .build();
            resultQueueToken = queueTokenRepository.save(queueToken);
        } else {
            int position = waitTokenList.size() +1;
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
        List<QueueToken> tokenList = queueTokenRepository.findByStatus(TokenStatus.PROCESS);
        Optional<QueueToken> queueToken = tokenList.stream()
                .filter(token ->
                           token.getUserId().equals(command.userId())
                        && token.getToken().equals(command.token())
                        && token.getStatus().equals(TokenStatus.PROCESS)
                )
                .findFirst();

        if (queueToken.isPresent()) {
            QueueToken token = queueToken.get();
            System.out.println(token);
            token.setStatus(TokenStatus.EXPIRED);
            token.setIsActive(UseYn.N);
            saveToken(token);

            positionDecreaseWaitingToken();
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
        QueueToken queueToken = getTokenInfo(token);
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
    public void expiredQueue(UUID userId) {
        QueueToken queueToken = getUserToken(userId);
        if (queueToken != null) {
            queueToken.setStatus(TokenStatus.EXPIRED);
            queueToken.setIsActive(UseYn.N);
            saveToken(queueToken);
        }
    }

    /**
     * 대기열 토큰의 대기순번, 대기시간 감소시켜주는 메서드
     * 메서드 실행 시 최대 1번 실행
     */
    public void positionDecreaseWaitingToken() {
        List<QueueToken> waitingTokens = queueTokenRepository.findByStatusOrderByPositionAsc(QueueToken.TokenStatus.WAIT);
        for (QueueToken waitingToken : waitingTokens) {
            int newPosition = waitingToken.getPosition() - 1;
            waitingToken.setPosition(newPosition);
            waitingToken.setWaitTime(newPosition * 5);

            if (newPosition == 0) {
                waitingToken.setStatus(QueueToken.TokenStatus.PROCESS);
                saveToken(waitingToken);
            }
        }
    }

    /**
     * 예약진행중인 토큰들 전부 조회
     * @return
     */
    public List<QueueToken> getAllProcessTokens() {
        return queueTokenRepository.findByStatus(TokenStatus.PROCESS);
    }


    /**
     * 토큰정보 조회
     * @param token
     * @return
     */
    public QueueToken getTokenInfo(String token) {
        return queueTokenRepository.findByToken(token);
    }

    /**
     * 토큰정보 저장
     * @param token
     */
    public void saveToken(QueueToken token) {
        queueTokenRepository.save(token);
    }

    /**
     * 토큰상태 및 대기열체크 후 서비스 이용단계로 진입
     * @param userId
     * @param token
     */
    public void tokenStatusAndWaitingCheckToProcess(UUID userId, String token) {
        QueueToken tokenInfo = getTokenInfo(token);
        if (tokenInfo.getStatus() != TokenStatus.WAIT) {
            throw new CustomException.QueueException(CustomException.QueueException.TOKEN_NOT_IN_QUEUE);
        }

        // 대기열 순번이 0인지 체크
        if (!isQueuePositionZero(token)) {
//            QueueToken queueToken = getUserToken(userId);
            int position = tokenInfo.getPosition();
            int waitTime = tokenInfo.getWaitTime();
            throw new CustomException.QueueException(CustomException.QueueException.TOKEN_NOT_POSITION_ZERO + String.format(" 현재 대기순번: %d, 남은 대기시간: %d ", position, waitTime));
        }

        // 토큰 상태를 PROCESS로 변경
        tokenInfo.setStatus(TokenStatus.PROCESS);
        saveToken(tokenInfo);
    }
}