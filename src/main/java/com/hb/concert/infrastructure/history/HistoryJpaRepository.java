package com.hb.concert.infrastructure.history;

import com.hb.concert.domain.history.History;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryJpaRepository extends JpaRepository<History, Long> {
}
