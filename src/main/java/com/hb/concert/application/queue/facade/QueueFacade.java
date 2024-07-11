package com.hb.concert.application.queue.facade;

import com.hb.concert.application.queue.command.QueueCommand;
import com.hb.concert.application.queue.service.QueueService;
import com.hb.concert.domain.queue.QueueToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QueueFacade {

    private final QueueService queueService;

    @Autowired
    public QueueFacade(QueueService queueService) {
        this.queueService = queueService;
    }

    /**
     * 대기열 토큰을 생성
     * @param command 토큰 생성 요청 정보
     * @return 생성된 토큰 정보
     */
    public QueueToken generateToken(QueueCommand.Generate command) {
        return queueService.generateToken(command);
    }

    /**
     * 대기열에서 다음 토큰을 처리
     * @return 처리된 토큰 정보
     */
    public void processCompletedToken(QueueCommand.TokenCompleted command) {
        queueService.processCompletedToken(command);
    }
}
