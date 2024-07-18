package com.hb.concert.infrastructure.history;

import com.hb.concert.domain.history.History;
import com.hb.concert.domain.history.HistoryRepository;
import org.springframework.stereotype.Repository;

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
}
