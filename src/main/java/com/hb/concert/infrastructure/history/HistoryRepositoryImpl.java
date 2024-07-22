package com.hb.concert.infrastructure.history;

import com.hb.concert.domain.history.History;
import com.hb.concert.domain.history.HistoryRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class HistoryRepositoryImpl implements HistoryRepository {

    private final HistoryJpaRepository historyJpaRepository;

    public HistoryRepositoryImpl(HistoryJpaRepository historyJpaRepository) {
        this.historyJpaRepository = historyJpaRepository;
    }

    @Override
    public void save(History history) {
        historyJpaRepository.save(history);
    }

    @Override
    public List<History> findByUserId(UUID userId) {
        return historyJpaRepository.findByUserId(userId);
    }
}
