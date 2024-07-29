package com.hb.concert.infrastructure.queueToken;

import com.hb.concert.domain.queueToken.QueueToken;
import com.hb.concert.domain.queueToken.repository.QueueTokenRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class QueueTokenRepositoryImpl implements QueueTokenRepository {

    private final QueueTokenJpaRepository queueTokenJpaRepository;

    public QueueTokenRepositoryImpl(QueueTokenJpaRepository queueTokenJpaRepository) {
        this.queueTokenJpaRepository = queueTokenJpaRepository;
    }

    @Override public Optional<QueueToken> getTokenInfo(String token) {
        return queueTokenJpaRepository.findByToken(token);
    }

    @Override public List<QueueToken> getProcessTokenList(String concertDetailId) {
        return queueTokenJpaRepository.getProcessTokenList(concertDetailId);
    }

    @Override public List<QueueToken> getWaitTokenList(String concertDetailId) {
        return queueTokenJpaRepository.getWaitTokenList(concertDetailId);
    }

    @Override public QueueToken save(QueueToken queueToken) {
        return queueTokenJpaRepository.save(queueToken);
    }

    @Override public List<QueueToken> getWaitTokens() {
        return queueTokenJpaRepository.getWaitTokens();
    }

    @Override public void saveAll(List<QueueToken> expiredTargetList) {
        queueTokenJpaRepository.saveAll(expiredTargetList);
    }

    @Override public List<QueueToken> getWaitPositionZeroTokens() {
        return queueTokenJpaRepository.findByPositionZeroAndStatusWait();
    }
}
