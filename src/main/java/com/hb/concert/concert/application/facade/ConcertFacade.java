package com.hb.concert.concert.application.facade;

import com.hb.concert.concert.entity.ConcertCommand;
import com.hb.concert.concert.entity.ConcertReservation;
import com.hb.concert.concert.entity.ViewData.ConcertInfo;
import com.hb.concert.concert.entity.ViewData.ScheduleInfo;
import com.hb.concert.concert.entity.ViewData.SeatInfo;
import com.hb.concert.concert.entity.service.ConcertService;
import com.hb.concert.payment.entity.service.PaymentService;
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
                throw new RuntimeException("예약정보에 대한 락을 획득하지 못했습니다.");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("예약정보에 대한 락을 획득하던 도중 서비스가 중단되었습니다.", e);
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
                throw new RuntimeException("예약만료에 대한 락을 획득하지 못했습니다.");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("예약만료에 대한 락을 획득하던 도중 서비스가 중단되었습니다.", e);
        }
    }

    public List<ConcertInfo> getConcertInfo() {
        return concertService.getConcertInfo();
    }

    public ScheduleInfo getScheduleInfo(ConcertCommand.Search searchInfoCommand) {
        return concertService.getScheduleInfo(searchInfoCommand.concertId());
    }

    public SeatInfo getSeatInfo(ConcertCommand.Search command) {
        return concertService.getSeatInfo(command.concertId(), command.concertDetailId());
    }
}