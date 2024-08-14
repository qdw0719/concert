package com.hb.concert.interfaces.batch;

import com.hb.concert.application.concert.facade.ConcertFacade;
import com.hb.concert.domain.dataplatform.service.DataPlatformService;
import com.hb.concert.domain.queueToken.service.QueueTokenRedisService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration @EnableScheduling
public class BatchJobConfig {

//    private final QueueTokenService queueTokenService;
    private final QueueTokenRedisService queueTokenService;
    private final ConcertFacade concertFacade;
    private final DataPlatformService dataPlatformService;

    public BatchJobConfig(QueueTokenRedisService queueTokenService, ConcertFacade concertFacade, DataPlatformService dataPlatformService) {
        this.queueTokenService = queueTokenService;
        this.concertFacade = concertFacade;
        this.dataPlatformService = dataPlatformService;
    }

    /**
     * 예약(좌석임시배정) 은 걸어놨지만 결제는 하지않는 사용자 만료처리
     */
    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void checkExpiredReservations() {
        concertFacade.expiredReservations();
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

    @Scheduled(fixedRate = 60000)
    public void dataplatformResend() {
        dataPlatformService.resend();
    }
}
