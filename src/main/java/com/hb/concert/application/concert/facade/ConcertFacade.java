package com.hb.concert.application.concert.facade;

import com.hb.concert.application.concert.ConcertCommand;
import com.hb.concert.domain.concert.ConcertReservation;
import com.hb.concert.domain.concert.ViewData;
import com.hb.concert.domain.concert.service.ConcertService;
import com.hb.concert.domain.payment.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service @Slf4j
public class ConcertFacade {

    private final ConcertService concertService;
    private final PaymentService paymentService;
    private final RedissonClient redissonClient;

    public ConcertFacade(ConcertService concertService, PaymentService paymentService, RedissonClient redissonClient) {
        this.concertService = concertService;
        this.paymentService = paymentService;
        this.redissonClient = redissonClient;
    }

    @Transactional
    public ConcertReservation createReservation(ConcertCommand.CreateReservation createReservationCommand) {
        RLock lock = redissonClient.getLock("concertReservationLock:" + createReservationCommand.concertDetailId());
        try {
            if (lock.tryLock(10, TimeUnit.SECONDS)) {
                try {
                    ConcertReservation reservation = concertService.createReservation(createReservationCommand.userId(), createReservationCommand.concertDetailId(), createReservationCommand.seatId());
                    paymentService.createPayment(reservation.getReservationId());
                    return reservation;
                }  finally {
                    lock.unlock();
                }
            } else {
                throw new RuntimeException("Could not acquire lock for creating reservation");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to acquire lock for creating reservation", e);
        }
    }

    @Transactional
    public void expiredReservations() {
        RLock lock = redissonClient.getLock("expiredReservationsLock");
        try {
            if (lock.tryLock(10, TimeUnit.SECONDS)) {
                try {
                    // 결제하지 않은 예약건 조회
                    List<ConcertReservation> notPaidReservations = concertService.notPaidReservations();

                    // 결제 유효시간 지난 예약id 조회
                    List<String> expiredReservationIdList = new ArrayList<>();
                    notPaidReservations.forEach(reservation -> {
                        expiredReservationIdList.addAll(paymentService.getEffectiveTimeAfterNow(reservation.getReservationId(), reservation.getReservationTime()));
                    });

                    // 예약 invalid 처리
                    expiredReservationIdList.forEach(reservationId -> {
                        concertService.getExpiredTargetList(reservationId);
                    });
                } finally {
                    lock.unlock();
                }
            } else {
                throw new RuntimeException("Could not acquire lock for expiring reservations");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to acquire lock for expiring reservations", e);
        }
    }

    public List<ViewData.ConcertInfo> getConcertInfo() {
        return concertService.getConcertInfo();
    }

    public ViewData.ScheduleInfo getScheduleInfo(ConcertCommand.Search searchInfoCommand) {
        return concertService.getScheduleInfo(searchInfoCommand.concertId());
    }

    public ViewData.SeatInfo getSeatInfo(ConcertCommand.Search command) {
        return concertService.getSeatInfo(command.concertId(), command.concertDetailId());
    }
}