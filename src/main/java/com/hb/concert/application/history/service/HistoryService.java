package com.hb.concert.application.history.service;

import com.hb.concert.application.history.command.HistoryCreateCommand;
import com.hb.concert.domain.history.History;
import com.hb.concert.domain.history.HistoryRepository;
import org.springframework.stereotype.Service;

@Service
public class HistoryService {

    private final HistoryRepository historyRepository;

    public HistoryService(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    public void saveHistory(HistoryCreateCommand.HistoryCreate command) {
        History history = new History().builder()
                .userId(command.userId())
                .type(command.type())
                .regDate(command.regDate())
                .status(command.status())
                .failReason(command.failReason())
                .build();
        historyRepository.save(history);
    }

}
