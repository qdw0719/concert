package com.hb.concert.domain.reservation.service;

import com.hb.concert.application.reservation.command.ReservationCommand;
import com.hb.concert.application.reservation.command.ReservationDetailCommand;
import com.hb.concert.support.CommonUtil;
import com.hb.concert.domain.common.enumerate.UseYn;
import com.hb.concert.domain.common.enumerate.ValidState;
import com.hb.concert.domain.reservation.Reservation;
import com.hb.concert.domain.reservation.ReservationDetail;
import com.hb.concert.domain.reservation.ReservationDetailRepository;
import com.hb.concert.domain.reservation.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.hb.concert.support.CommonUtil.padLeftZeros;
import static com.hb.concert.support.CommonUtil.padRightZeros;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationDetailRepository reservationDetailRepository;

    public ReservationService(ReservationRepository reservationRepository, ReservationDetailRepository reservationDetailRepository) {
        this.reservationRepository = reservationRepository;
        this.reservationDetailRepository = reservationDetailRepository;
    }

    /**
     * 예약 생성
     *
     * @param command 예약 생성
     * @return ReservationResponse 예약 내역
     */
    @Transactional
    public Reservation createReservation(ReservationCommand.Create command) {
        Reservation reservation = new Reservation().builder()
                .reservationId(generateReservationId())
                .userId(command.userId())
                .concertId(command.concertId())
                .concertDetailId(command.concertDetailId())
                .reservationTime(LocalDateTime.now())
                .temporaryGrantTime(LocalDateTime.now().plusMinutes(5))
                .isPaid(UseYn.N)
                .validState(ValidState.VALID)
                .build();

        saveReservation(reservation);

        return reservation;
    }

    /**
     * 예약번호 생성
     *
     * @param
     * @return String token
     * */
    private String generateReservationId() {
        String reservationIdStartStr = "reservation_";
        String lastReservationId = reservationRepository.findTopByOrderByIdDesc()
                .map(Reservation::getReservationId)
                .orElse(padRightZeros(reservationIdStartStr, 4));

        String newReservationId = reservationIdStartStr + padLeftZeros(lastReservationId.split("_")[1] + 1, 4);
        return newReservationId;
    }

    public Reservation getReservationInfoByUser(UUID userId) {
        return reservationRepository.findTopByUserIdOrderByUserIdDesc(userId);
    }

    public Reservation getReservationInfoByUserToday(UUID userId) {
        return reservationRepository.findTopByUserIdAndReservationTimeBetweenOrderByUserIdDesc(userId, LocalDateTime.now().minusMinutes(5), LocalDateTime.now());
    }

    /**
     * 예약 상세 생성
     *
     * @param command 예약 생성
     */
    @Transactional
    public void createReservationDetails(ReservationDetailCommand.CreateReservationDetail command, List<Integer> concertSeatIdList) {
        List<ReservationDetail> reservationDetailList = new ArrayList<>();
        for (Integer seatNumber : concertSeatIdList) {
            reservationDetailList.add(
                    ReservationDetail.builder()
                            .reservationId(command.reservationId())
                            .concertSeatId(seatNumber)
                            .build()
            );
        }
        reservationDetailRepository.saveAll(reservationDetailList);
    }

    public List<Integer> getConcertSeatIdByReservationId(String reservationId) {
        return reservationDetailRepository.findConcertSeatIdByReservationId(reservationId);
    }


    /**
     * 결제하지 않고 임시예약(임시좌석)시간이 지난 reservation건들 조회
     *
     * @return List<Reservation>
     */
    public List<Reservation> getExpiredTargetList() {
        return reservationRepository.findByIsPaidAndTemporaryGrantTimeBefore(UseYn.N, LocalDateTime.now());
    }

    /**
     * 예약내역 저장
     * @param reservation
     */
    @Transactional
    public void saveReservation(Reservation reservation) {
        reservationRepository.save(reservation);
    }


    /**
     * 오늘 날짜에 예약한 유저 조회
     * @return List<UUID>
     */
    public List<UUID> findUserNotReservationToday() {
        LocalDate today = LocalDate.now();
        LocalDateTime startTime = today.atStartOfDay();
        LocalDateTime endTime = today.atTime(LocalTime.MAX);
        return reservationRepository.findUserNotReservationToday(startTime, endTime);
    }

    public boolean hasReservation(String reservationId) {
        return reservationRepository.countByReservationId(reservationId) > 0;
    }

    public Reservation getReservationInfo(String reservationId) {
        return reservationRepository.findByReservationId(reservationId);
    }
}
