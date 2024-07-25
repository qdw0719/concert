package com.hb.concert.infrastructure.queue;

import com.hb.concert.domain.queue.QueueToken;
import com.hb.concert.domain.queue.QueueToken.TokenStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QueueTokenJpaRepository extends JpaRepository<QueueToken, Long> {
    int countByStatus(TokenStatus status);

    QueueToken findByToken(String token);

    Optional<QueueToken> findFirstByStatusOrderByPositionAsc(TokenStatus status);

    @Lock(LockModeType.PESSIMISTIC_READ)
    List<QueueToken> findByStatus(TokenStatus status);

    QueueToken findByUserIdAndStatus(UUID userId, TokenStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<QueueToken> findByStatusOrderByPositionAsc(QueueToken.TokenStatus status);

    QueueToken findByUserIdAndToken(UUID userId, String token);
}
