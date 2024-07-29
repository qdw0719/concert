package com.hb.concert.domain.concert.service;

import com.hb.concert.domain.concert.ConcertDetail;
import com.hb.concert.domain.concert.ConcertReservation;
import com.hb.concert.domain.concert.ViewData.ScheduleInfo;
import com.hb.concert.domain.concert.ViewData.ConcertInfo;
import com.hb.concert.domain.concert.ViewData.SeatInfo;
import com.hb.concert.domain.concert.repository.ConcertRepository;
import com.hb.concert.domain.concert.repository.ConcertReservationRepository;
import com.hb.concert.domain.exception.CustomException.NotFoundException;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class ConcertService {

    private final ConcertRepository concertRepository;
    private final ConcertReservationRepository concertReservationRepository;
    private final RedissonClient redissonClient;

    public ConcertService(ConcertRepository concertRepository, ConcertReservationRepository concertReservationRepository, RedissonClient redissonClient) {
        this.concertRepository = concertRepository;
        this.concertReservationRepository = concertReservationRepository;
        this.redissonClient = redissonClient;
    }

    /**
     * 콘서트 정보를 조회한다
     * (현재날짜 기준으로 예약 가능한 콘서트만 가져옴)
     * @return List<ConcertInfo>
     */
    public List<ConcertInfo> getConcertInfo() {
        return concertRepository.getConcertInfo();
    }

    /**
     * 콘서트 일정 정보를 조회한다
     * (현재날짜 기준으로 이후 일정의 일정만 조회)
     * @param concertId
     * @return ScheduleInfo
     */
    public ScheduleInfo getScheduleInfo(String concertId) {
        return concertRepository.getScheduleInfo(concertId);
    }

    /**
     * 콘서트 좌석정보를 조회한다
     * (선택한 일정에서 capacuty 조회 후 좌석 기본정보 테이블에서 조회 해당 seq까지 조회)
     * @param concertId
     * @param concertDetailId
     * @return SeatInfo 
     */
    public SeatInfo getSeatInfo(String concertId, String concertDetailId) {
        return concertRepository.getSeatInfo(concertId, concertDetailId);
    }

    /**
     * 결재하지 않은 예약건을 조회한다
     * @return List<ConcertReservation>
     */
    public List<ConcertReservation> notPaidReservations() {
        return concertReservationRepository.getReservationNotPaid();
    }

    /**
     * 에약정보를 생성한다.(좌석 임시배정)
     * @param userId
     * @param concertDetailId
     * @param seatId
     * @return ConcertReservation
     */
    @Transactional
    public ConcertReservation createReservation(UUID userId, String concertDetailId, List<Integer> seatId) {
        RLock lock = redissonClient.getLock("concertReservationLock:" + concertDetailId);
        try {
            if (lock.tryLock(10, TimeUnit.SECONDS)) {
                try {
                    ConcertReservation reservation = new ConcertReservation();
                    reservation.createReservation(userId, concertDetailId, seatId);
                    concertReservationRepository.save(reservation);
                    reduceAvailableSeatCount(concertDetailId, seatId.size());
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

    /**
     * 해당 일정에 예약가능한 좌석수를 선택한 좌석 수만큼 감소시킨다
     * @param concertDetailId
     * @param selectedSeatCount
     */
    @Transactional
    public void reduceAvailableSeatCount(String concertDetailId, int selectedSeatCount) {
        RLock lock = redissonClient.getLock("seatLock:" + concertDetailId);
        try {
            if (lock.tryLock(10, TimeUnit.SECONDS)) {
                try {
                    ConcertDetail concertDetail = concertRepository.getConcertDetailInfo(concertDetailId).orElseThrow(() -> new NotFoundException(NotFoundException.CONCERT_SCHEDULE_NOT_FOUND));
                    concertDetail.reduceAvailableSeat(selectedSeatCount);
                    concertRepository.concertDetailSave(concertDetail);
                } finally {
                    lock.unlock();
                }
            } else {
                throw new RuntimeException("Could not acquire lock for reducing available seat count");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to acquire lock for reducing available seat count", e);
        }
    }

    /**
     * 결제가 완료되었을 때 예약데이터 중 결제여부를 Y로 저장한다.
     * @param userId
     * @param reservationId
     * @return ConcertReservation
     */
    @Transactional
    public ConcertReservation completeReserved(UUID userId, String reservationId) {
        ConcertReservation reservation = concertReservationRepository.getReservationInfoByReservationId(userId, reservationId);
        reservation.successPaid();
        concertReservationRepository.save(reservation);
        return reservation;
    }

    public void getExpiredTargetList(String reservationId) {
        List<ConcertReservation> reservationInfo = concertReservationRepository.getReservationInfo(reservationId);
        reservationInfo.forEach(reservation -> reservation.failPaid());
        concertReservationRepository.saveAll(reservationInfo);
    }
}