package com.hb.concert.domain.queue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface QueueTokenRepository {

    QueueToken save(QueueToken queueToken);

    QueueToken findByToken(String token);

    List<QueueToken> findByStatus(QueueToken.TokenStatus status);

    QueueToken findByUserIdAndStatus(UUID userId, QueueToken.TokenStatus status);

    List<QueueToken> findAll();

    List<QueueToken> findByStatusOrderByPositionAsc(QueueToken.TokenStatus status);

    int countByStatus(QueueToken.TokenStatus status);
}
