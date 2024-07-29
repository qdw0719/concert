package com.hb.concert.infrastructure.queueToken;

import com.hb.concert.domain.queueToken.QueueToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface QueueTokenJpaRepository extends JpaRepository<QueueToken, Long> {
    Optional<QueueToken> findByToken(String token);

    @Query("select qt from QueueToken qt where qt.concertDetailId = :concertDetailId and qt.status = 'PROCESS'")
    List<QueueToken> getProcessTokenList(String concertDetailId);

    @Query("select qt from QueueToken qt where qt.concertDetailId = :concertDetailId and qt.status = 'WAIT'")
    List<QueueToken> getWaitTokenList(String concertDetailId);

    @Query("select qt from QueueToken qt where qt.status = 'WAIT'")
    List<QueueToken> getWaitTokens();

    @Query("select qt from QueueToken qt where qt.position = 0 and qt.status = 'WAIT'")
    List<QueueToken> findByPositionZeroAndStatusWait();
}
