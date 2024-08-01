package com.hb.concert.domain.queueToken.service;

import com.hb.concert.domain.exception.CustomException.QueueTokenException;
import com.hb.concert.domain.queueToken.QueueTokenConfig;
import com.hb.concert.domain.queueToken.QueueTokenRedis;
import com.hb.concert.domain.queueToken.TokenStatus;
import com.hb.concert.domain.queueToken.ViewData.TokenInfo;
import com.hb.concert.support.config.util.JwtUtil;
import org.redisson.api.RMap;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class QueueTokenRedisService {

    private final RedissonClient redissonClient;
    private final JwtUtil jwtUtil;

    private static final String PROCESS_QUEUE = "queueToken:process";
    private static final String WAIT_QUEUE = "queueToken:wait";
    private static final String TOKEN_MAP = "tokenMap";

    public QueueTokenRedisService(RedissonClient redissonClient, JwtUtil jwtUtil) {
        this.redissonClient = redissonClient;
        this.jwtUtil = jwtUtil;
    }

    /***
     * 토큰을 생성한다.
     * @param userId 유저 ID
     * @param concertDetailId 콘서트 상세 ID
     * @return 생성된 토큰 정보
     */
    @Transactional
    public TokenInfo issueToken(UUID userId, String concertDetailId) {
        RScoredSortedSet<String> waitQueue = redissonClient.getScoredSortedSet(WAIT_QUEUE + ":" + concertDetailId);
        RMap<String, QueueTokenRedis> tokenMap = redissonClient.getMap(TOKEN_MAP);

        int position = waitQueue.size() + 1;
        String token = jwtUtil.generateToken(userId, position, concertDetailId);
        TokenStatus status = TokenStatus.WAIT;

        waitQueue.add(position, token);

        QueueTokenRedis queueToken = new QueueTokenRedis(token, concertDetailId, position, status);
        tokenMap.put(token, queueToken);

        return new TokenInfo(token, position);
    }
//    public TokenInfo generateToken(UUID userId, String concertDetailId) {
//        RScoredSortedSet<String> processQueue = redissonClient.getScoredSortedSet(PROCESS_QUEUE + ":" + concertDetailId);
//        RScoredSortedSet<String> waitQueue = redissonClient.getScoredSortedSet(WAIT_QUEUE + ":" + concertDetailId);
//        RMap<String, QueueTokenRedis> tokenMap = redissonClient.getMap(TOKEN_MAP);
//
//        String token;
//        int position;
//        TokenStatus status;
//
//        if (processQueue.size() < QueueTokenConfig.ACTIVE_USER_INTERVAL && waitQueue.isEmpty()) {
//            position = 0;
//            token = jwtUtil.generateToken(userId, position, concertDetailId);
//            status = TokenStatus.PROCESS;
//            processQueue.add(position, token);
//        } else {
//            position = waitQueue.size() + 1;
//            token = jwtUtil.generateToken(userId, position, concertDetailId);
//            status = TokenStatus.WAIT;
//            waitQueue.add(position, token);
//        }
//
//        QueueTokenRedis queueToken = new QueueTokenRedis(token, concertDetailId, (int) position, status);
//        tokenMap.put(token, queueToken);
//
//        return new TokenInfo(token, position);
//    }

    /***
     * 토큰 정보를 조회한다.
     * @param tokenStr 토큰 문자열
     * @return 조회된 토큰 정보
     */
    public QueueTokenRedis getTokenInfo(String tokenStr) {
        RMap<String, QueueTokenRedis> tokenMap = redissonClient.getMap(TOKEN_MAP);
        QueueTokenRedis queueToken = tokenMap.get(tokenStr);
        if (queueToken == null) {
            throw new QueueTokenException(QueueTokenException.TOKEN_UNAUTHORIZED);
        }
        return queueToken;
    }

    /***
     * 대기열 정보 조회 메서드
     * expiredAt을 대기열 조회할때마다 update해준다
     * @param token 토큰 문자열
     * @return 대기열 정보
     */
    @Transactional
    public TokenInfo getWaitingInfo(String token) {
        RMap<String, QueueTokenRedis> tokenMap = redissonClient.getMap(TOKEN_MAP);
        QueueTokenRedis queueToken = getTokenInfo(token);
        tokenMap.put(token, queueToken);
        return new TokenInfo(token, queueToken.getPosition());
    }

    /***
     * 결제가 완료되었을 때 토큰을 만료시킨다.
     * @param token 토큰 문자열
     */
    @Transactional
    public void expiredTokenAfterPayment(String token) {
        RMap<String, QueueTokenRedis> tokenMap = redissonClient.getMap(TOKEN_MAP);
        QueueTokenRedis queueToken = getTokenInfo(token);
        queueToken.setTokenStatusExpired();
        tokenMap.put(token, queueToken);
    }

    /***
     * 대기상태의 토큰 상태를 활성상태로 업데이트한다.
     * @param token 토큰 문자열
     */
    @Transactional
    public void updateWaitToProcessStatus(String token) {
        RMap<String, QueueTokenRedis> tokenMap = redissonClient.getMap(TOKEN_MAP);
        QueueTokenRedis queueToken = getTokenInfo(token);
        queueToken.setTokenStatusProcess();
        tokenMap.put(token, queueToken);

        String concertDetailId = queueToken.getConcertDetailId();
        RScoredSortedSet<String> waitQueue = redissonClient.getScoredSortedSet(WAIT_QUEUE + ":" + concertDetailId);
        RScoredSortedSet<String> processQueue = redissonClient.getScoredSortedSet(PROCESS_QUEUE + ":" + concertDetailId);

        waitQueue.remove(token);
        processQueue.add(queueToken.getPosition(), token);
    }

    /***
     * 스케줄을 돌면서 expiredtime 체크해서 만료처리
     * 1분마다 실행된다.
     */
    @Transactional
    public void expiredToken() {
        RMap<String, QueueTokenRedis> tokenMap = redissonClient.getMap(TOKEN_MAP);
        RScoredSortedSet<String> waitQueue = redissonClient.getScoredSortedSet(WAIT_QUEUE);

        for (String token : waitQueue) {
            QueueTokenRedis queueToken = tokenMap.get(token);
            if (queueToken.isTokenExpiredTarget()) {
                queueToken.setTokenStatusExpired();
                tokenMap.put(token, queueToken);
                waitQueue.remove(token);
            }
        }
    }

    /***
     * 1분마다 50명씩 대기열에 있는 토큰들을 활성 상태로 변경한다.
     */
    @Transactional
    public void activateTokens() {
        RMap<String, QueueTokenRedis> tokenMap = redissonClient.getMap(TOKEN_MAP);
        RScoredSortedSet<String> waitQueue = redissonClient.getScoredSortedSet(WAIT_QUEUE);

        int activatedCount = 0;
        for (String token : waitQueue) {
            if (activatedCount >= QueueTokenConfig.ACTIVE_USER_INTERVAL) {
                break;
            }

            QueueTokenRedis queueToken = tokenMap.get(token);
            queueToken.setTokenStatusProcess();
            tokenMap.put(token, queueToken);

            String concertDetailId = queueToken.getConcertDetailId();
            RScoredSortedSet<String> processQueue = redissonClient.getScoredSortedSet(PROCESS_QUEUE + ":" + concertDetailId);

            processQueue.add(queueToken.getPosition(), token);
            waitQueue.remove(token);

            activatedCount++;
        }
    }
//    public void processedToken() {
//        RMap<String, QueueTokenRedis> tokenMap = redissonClient.getMap(TOKEN_MAP);
//        RScoredSortedSet<String> processQueue = redissonClient.getScoredSortedSet(PROCESS_QUEUE);
//
//        for (String token : processQueue) {
//            QueueTokenRedis queueToken = tokenMap.get(token);
//            queueToken.setTokenStatusProcess();
//            tokenMap.put(token, queueToken);
//        }
//    }
}
