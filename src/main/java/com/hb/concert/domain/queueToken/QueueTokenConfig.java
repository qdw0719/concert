package com.hb.concert.domain.queueToken;

public interface QueueTokenConfig {
    // 최대 활성화 시킬 유저 수
    public static final Integer MAX_ACTIVE_USER = 50;

    // 토큰 만료시킬 시간
    public static final Integer EXPRIED_AT_MINUTES = 5;
}