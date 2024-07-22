package com.hb.concert.domain.history;

import java.util.List;
import java.util.UUID;

public interface HistoryRepository {
    void save(History history);

    List<History> findByUserId(UUID userId);
}
