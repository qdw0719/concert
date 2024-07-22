package com.hb.concert.infrastructure.history;

import com.hb.concert.domain.history.History;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HistoryJpaRepository extends JpaRepository<History, Long> {
    List<History> findByUserId(UUID userId);
}
