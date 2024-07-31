package com.hb.concert.domain.queueToken;

public interface QueueTokenConfig {
    // 한번에 활성화 시킬 유저 수
    public static final Integer ACTIVE_USER_INTERVAL = 50;

    // 토큰 만료시킬 시간
    public static final Integer EXPRIED_AT_MINUTES = 5;
}