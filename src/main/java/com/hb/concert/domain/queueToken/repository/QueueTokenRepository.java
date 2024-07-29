package com.hb.concert.domain.queueToken.repository;

import com.hb.concert.domain.queueToken.QueueToken;

import java.util.List;
import java.util.Optional;

public interface QueueTokenRepository {
    Optional<QueueToken> getTokenInfo(String token);

    List<QueueToken> getProcessTokenList(String concertDetailId);

    List<QueueToken> getWaitTokenList(String concertDetailId);

    QueueToken save(QueueToken queueToken);

    List<QueueToken> getWaitTokens();

    void saveAll(List<QueueToken> expiredTargetList);

    List<QueueToken> getWaitPositionZeroTokens();
}
