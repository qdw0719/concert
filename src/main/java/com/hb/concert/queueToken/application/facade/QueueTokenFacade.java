package com.hb.concert.queueToken.application.facade;

import com.hb.concert.queueToken.application.QueueTokenCommand;
import com.hb.concert.queueToken.entity.ViewData.TokenInfo;
import com.hb.concert.queueToken.entity.service.QueueTokenRedisService;
//import com.hb.concert.queueToken.entity.service.QueueTokenService;
import org.springframework.stereotype.Service;

@Service
public class QueueTokenFacade {

//    private final QueueTokenService queueTokenService;
    private final QueueTokenRedisService queueTokenService;

    public QueueTokenFacade(QueueTokenRedisService queueTokenService) {
        this.queueTokenService = queueTokenService;
    }

    public TokenInfo issueToken(QueueTokenCommand.Create command) {
        return queueTokenService.issueToken(command.userId(), command.concertDetailId());
    }

    public TokenInfo getWaitingInfo(QueueTokenCommand.Search command) {
        return queueTokenService.getWaitingInfo(command.token());
    }
}
