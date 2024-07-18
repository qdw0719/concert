package com.hb.concert.application.queue.facade;

import com.hb.concert.application.queue.command.QueueCommand;
import com.hb.concert.support.config.util.JwtUtil;
import com.hb.concert.domain.queue.service.QueueService;
import com.hb.concert.domain.queue.QueueToken;
import com.hb.concert.domain.reservation.Reservation;
import com.hb.concert.domain.reservation.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class QueueFacade {

    private final QueueService queueService;
    private final ReservationService reservationService;
    private final JwtUtil jwtUtil;

    @Autowired
    public QueueFacade(QueueService queueService, ReservationService reservationService, JwtUtil jwtUtil) {
        this.queueService = queueService;
        this.reservationService = reservationService;
        this.jwtUtil = jwtUtil;
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

    /**
     * 예약 후 또는 토큰 발급 후 아무 작업도 하지 않는 대상 만료처리
     */
    public void compulsoryExpiredTokens() {
        List<QueueToken> processTokens = queueService.getAllProcessTokens();

        boolean isExpired = false;
        for (QueueToken token : processTokens) {
            UUID userId = jwtUtil.getUserIdFromToken(token.getToken());
            Reservation reservationInfo = reservationService.getReservationInfoByUserToday(userId);

            List<UUID> expiredUserReservationInfo = reservationService.findUserNotReservationToday();

            if (reservationInfo != null || !expiredUserReservationInfo.contains(token.getUserId())) {
                isExpired = true;

                queueService.expiredQueue(userId);
            }
        }

        if (isExpired) {
            queueService.positionDecreaseWaitingToken();
        }
    }
}