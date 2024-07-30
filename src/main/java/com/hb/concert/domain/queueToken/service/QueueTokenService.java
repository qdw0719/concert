package com.hb.concert.domain.queueToken.service;

import com.hb.concert.domain.exception.CustomException;
import com.hb.concert.domain.queueToken.QueueToken;
import com.hb.concert.domain.queueToken.TokenStatus;
import com.hb.concert.domain.queueToken.QueueTokenConfig;
import com.hb.concert.domain.queueToken.ViewData.TokenInfo;
import com.hb.concert.domain.queueToken.repository.QueueTokenRepository;
import com.hb.concert.support.CommonUtil;
import com.hb.concert.support.config.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service @Slf4j
public class QueueTokenService {

    private final QueueTokenRepository queueTokenRepository;
    private final JwtUtil jwtUtil;

    public QueueTokenService(QueueTokenRepository queueTokenRepository, JwtUtil jwtUtil) {
        this.queueTokenRepository = queueTokenRepository;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 토큰을 생성한다.
     * @param userId
     * @param concertDetailId
     * @return TokenInfo
     */
    @Transactional
    public TokenInfo generateToken(UUID userId, String concertDetailId) {
        String token;
        int position;
        TokenStatus status;

        // 현재 서비스 진행중인 토큰 목록 조회
        List<QueueToken> processedTokenList = queueTokenRepository.getProcessTokenList(concertDetailId);
        // 대기 중인 토큰 목록 조회
        List<QueueToken> waitTokenList = queueTokenRepository.getWaitTokenList(concertDetailId);

        if (processedTokenList.size() < QueueTokenConfig.MAX_ACTIVE_USER && waitTokenList.size() == 0) {
            // 서비스 이용중인 인원이 최대 인원보다 적으면 즉시 서비스 이용가능한 토큰 생성
            position = 0;
            token = jwtUtil.generateToken(userId, position, concertDetailId);
            status = TokenStatus.PROCESS;
        } else {
            // 서비스 이용중인 인원이 최대인원일 경우 대기열에 진입
            position = waitTokenList.size() +1;
            status = TokenStatus.WAIT;
            token = jwtUtil.generateToken(userId, position, concertDetailId);
        }

        QueueToken queueToken = new QueueToken();
        queueToken.generateToken(token, concertDetailId, position, status);
        queueTokenRepository.save(queueToken);

        return new TokenInfo(token, position);
    }

    public QueueToken getTokenInfo(String tokenStr) {
        return queueTokenRepository.getTokenInfo(tokenStr).orElseThrow(() ->
                new CustomException.QueueTokenException(CustomException.QueueTokenException.TOKEN_UNAUTHORIZED)
        );
    }

    /***
     * 대기열 정보 조회 메서드
     * expiredAt을 대기열 조회할때마다 update해준다
     *
     * @param token
     * @return TokenInfo
     */
    @Transactional
    public TokenInfo getWaitingInfo(String token) {
        int position = 0;

        QueueToken queueToken = getTokenInfo(token);
        position = queueToken.getPosition();
        queueToken.updateExpiredAt();
        queueTokenRepository.save(queueToken);

        return new TokenInfo(token, position);
    }

    /**
     * 결제가 완료되었을 때 토큰을 만료시킨다
     * @param token
     */
    @Transactional
    public void expiredTokenAfterPayment(String token) {
        QueueToken queueToken = getTokenInfo(token);
        queueToken.setTokenStatusExpired();
        queueTokenRepository.save(queueToken);
    }

    /**
     * 대기상태의 토큰 상태를 활성상태로 update한다
     * @param token
     */
    @Transactional
    public void updateWaitToProcessStatus(String token) {
        QueueToken queueToken = getTokenInfo(token);
        queueToken.setTokenStatusProcess();
        queueTokenRepository.save(queueToken);
    }

    /**
     * 대기상태의 토큰들의 대기순번은 1씩 감소시킨다.
     */
    @Transactional
    public void waitTokenPositionReduce() {
        List<QueueToken> waitTokens = queueTokenRepository.getWaitTokens();
        if (!CommonUtil.isListNull(waitTokens)) {
            List<QueueToken> reduceTargetList = new ArrayList<>();
            waitTokens.forEach(token -> {
                token.reducePosition();
                reduceTargetList.add(token);
            });
            queueTokenRepository.saveAll(reduceTargetList);
        }
    }

    /**
     * 스케줄 둘면서 대기열에 있는 유령토큰을 제거한다
     * 1분마다 실행
     */
    @Transactional
    public void expiredToken() {
        // 대기준인 모든 토큰 조회
        List<QueueToken> waitTokens = queueTokenRepository.getWaitTokens();
        if (!CommonUtil.isListNull(waitTokens)) {
            List<QueueToken> expiredTargetList = new ArrayList<>();
            waitTokens.forEach(token -> {
                if (token.isTokenExpiredTarget()) {
                    expiredTargetList.add(token);
                    token.setTokenStatusExpired();
                }
            });
            log.info("토큰만료 스케줄러 :: 만료처리 한 토큰 {} 건", expiredTargetList);
            queueTokenRepository.saveAll(expiredTargetList);
        }
    }

    @Transactional
    public void processedToken() {
        // 대기순번이 0인 토큰조회
        List<QueueToken> waitPositionZeroTokens = queueTokenRepository.getWaitPositionZeroTokens();
        if (!CommonUtil.isListNull(waitPositionZeroTokens)) {
            List<QueueToken> setProcessTargetTokens = new ArrayList<>();
            waitPositionZeroTokens.forEach(token -> {
                token.setTokenStatusProcess();
            });
            queueTokenRepository.saveAll(setProcessTargetTokens);
        }
    }
}
