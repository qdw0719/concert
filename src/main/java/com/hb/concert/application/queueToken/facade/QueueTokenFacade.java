package com.hb.concert.application.queueToken.facade;

import com.hb.concert.application.queueToken.QueueTokenCommand;
import com.hb.concert.domain.queueToken.ViewData.TokenInfo;
import com.hb.concert.domain.queueToken.service.QueueTokenRedisService;
//import com.hb.concert.domain.queueToken.service.QueueTokenService;
import org.springframework.stereotype.Service;

@Service
public class QueueTokenFacade {

//    private final QueueTokenService queueTokenService;
    private final QueueTokenRedisService queueTokenService;

    public QueueTokenFacade(QueueTokenRedisService queueTokenService) {
        this.queueTokenService = queueTokenService;
    }

    public TokenInfo generateToken(QueueTokenCommand.Create command) {
        return queueTokenService.generateToken(command.userId(), command.concertDetailId());
    }

    public TokenInfo getWaitingInfo(QueueTokenCommand.Search command) {
        return queueTokenService.getWaitingInfo(command.token());
    }
}
