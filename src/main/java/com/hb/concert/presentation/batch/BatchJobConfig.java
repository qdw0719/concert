package com.hb.concert.presentation.batch;

import com.hb.concert.application.queue.facade.QueueFacade;
import com.hb.concert.application.reservation.facade.ReservationFacade;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class BatchJobConfig {

    private final ReservationFacade reservationFacade;
    private final QueueFacade queueFacade;

    public BatchJobConfig(ReservationFacade reservationFacade, QueueFacade queueFacade) {
        this.reservationFacade = reservationFacade;
        this.queueFacade = queueFacade;
    }

    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void checkExpiredReservations() {
        reservationFacade.expireReservations();
    }

    @Scheduled(fixedRate = 60000)
    public void checkExpiredTokens() {
        queueFacade.compulsoryExpiredTokens();
    }
}
