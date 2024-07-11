package com.hb.concert.application.reservation.service;

import com.hb.concert.application.concert.command.ConcertCommand;
import com.hb.concert.application.reservation.command.ReservationCommand;
import com.hb.concert.common.CommonUtil;
import com.hb.concert.domain.common.enumerate.UseYn;
import com.hb.concert.domain.common.enumerate.ValidState;
import com.hb.concert.domain.reservation.Reservation;
import com.hb.concert.domain.reservation.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.hb.concert.common.CommonUtil.padLeftZeros;
import static com.hb.concert.common.CommonUtil.padRightZeros;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
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

        reservationRepository.save(reservation);

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
        String lastReservationId = reservationRepository.findTopByOrderByIdDesc().getReservationId();

        if (CommonUtil.isNull(lastReservationId)) {
            lastReservationId = padRightZeros(reservationIdStartStr, 4); //뭐.. 사이에 concertId 넣을까 말까,,,
        }

        String newReservationId = reservationIdStartStr + padLeftZeros(lastReservationId.split("_")[1] + 1, 4);
        return newReservationId;
    }

    public Reservation getReservationInfoByUser(UUID userId) {
        return reservationRepository.findTopByOrderByUserIdDesc(userId);
    }

}
