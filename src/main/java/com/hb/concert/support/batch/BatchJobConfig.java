package com.hb.concert.support.batch;

import com.hb.concert.concert.application.facade.ConcertFacade;
import com.hb.concert.queueToken.entity.service.QueueTokenRedisService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration @EnableScheduling
public class BatchJobConfig {

//    private final QueueTokenService queueTokenService;
    private final QueueTokenRedisService queueTokenService;
    private final ConcertFacade concertFacade;

    public BatchJobConfig(QueueTokenRedisService queueTokenService, ConcertFacade concertFacade) {
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
     * expiredtime 체크해서 만료처리
     * */
    @Scheduled(fixedRate = 60000)
    public void checkExpiredTokens() {
        queueTokenService.expiredToken();
    }

    /**
     * 50명씩 활성화
     * 00분에 시작해서 5분주기로 활성화
     * */
    @Scheduled(cron = "0 */5 * * * *")
    public void checkProcessedTokens() throws InterruptedException {
        Thread.sleep(30000);
        queueTokenService.activateTokens();
    }
}
