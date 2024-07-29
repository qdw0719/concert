package com.hb.concert.interfaces.batch;

import com.hb.concert.application.concert.facade.ConcertFacade;
import com.hb.concert.domain.queueToken.service.QueueTokenService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration @EnableScheduling
public class BatchJobConfig {

    private final QueueTokenService queueTokenService;
    private final ConcertFacade concertFacade;

    public BatchJobConfig(QueueTokenService queueTokenService, ConcertFacade concertFacade) {
        this.queueTokenService = queueTokenService;
        this.concertFacade = concertFacade;
    }

    /**
     * 예약(좌석임시배정) 은 걸어놨지만 결제는 하지않는 사용자 만료처리
     */
    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void checkExpiredReservations() {
        concertFacade.expiredReservations();
    }

    /**
     * 대기열 입장 후 아무런 api호출이 없는 사용자 만료처리
     * */
    @Scheduled(fixedRate = 60000)
    public void checkExpiredTokens() {
        queueTokenService.expiredToken();
        queueTokenService.waitTokenPositionReduce();
    }

    /**
     * 대기순번 0인 토큰들은 활성화
     * */
    @Scheduled(fixedRate = 60000)
    public void checkProcessedTokens() {
        queueTokenService.processedToken();
    }
}
