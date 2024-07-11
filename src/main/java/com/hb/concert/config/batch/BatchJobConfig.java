package com.hb.concert.config.batch;

import com.hb.concert.application.reservation.facade.ReservationFacade;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class BatchJobConfig {

    private final ReservationFacade reservationFacade;

    public BatchJobConfig(ReservationFacade reservationFacade) {
        this.reservationFacade = reservationFacade;
    }

    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void checkExpiredReservations() {
        reservationFacade.expireReservations();
    }
}
