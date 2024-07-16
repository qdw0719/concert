package com.hb.concert.infrastructure.queue;

import com.hb.concert.domain.queue.QueueToken;
import com.hb.concert.domain.queue.QueueToken.TokenStatus;
import com.hb.concert.domain.queue.QueueTokenRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class QueueTokenRepositoryImpl implements QueueTokenRepository {

    private final QueueTokenJpaRepository queueTokenJpaRepository;

    public QueueTokenRepositoryImpl(QueueTokenJpaRepository queueTokenJpaRepository) {
        this.queueTokenJpaRepository = queueTokenJpaRepository;
    }

    @Override
    public QueueToken save(QueueToken queueToken) {
        return queueTokenJpaRepository.save(queueToken);
    }

    @Override
    public QueueToken findByToken(String token) {
        return queueTokenJpaRepository.findByToken(token);
    }

    @Override
    public List<QueueToken> findByStatus(TokenStatus status) {
        return queueTokenJpaRepository.findByStatus(status);
    }

    @Override
    public QueueToken findByUserIdAndStatus(UUID userId, TokenStatus status) {
        return queueTokenJpaRepository.findByUserIdAndStatus(userId, status);
    }

    @Override
    public List<QueueToken> findAll() {
        return queueTokenJpaRepository.findAll();
    }

    @Override
    public List<QueueToken> findByStatusOrderByPositionAsc(TokenStatus status) {
        return queueTokenJpaRepository.findByStatusOrderByPositionAsc(status);
    }

    @Override
    public int countByStatus(TokenStatus status) {
        return queueTokenJpaRepository.countByStatus(status);
    }
}
