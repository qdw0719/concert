package com.hb.concert.application.reservation.service;

import com.hb.concert.application.reservation.command.ReservationDetailCommand;
import com.hb.concert.domain.reservation.ReservationDetail;
import com.hb.concert.domain.reservation.ReservationDetailRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReservationDetailService {

    private final ReservationDetailRepository reservationDetailRepository;

    public ReservationDetailService(ReservationDetailRepository reservationDetailRepository) {
        this.reservationDetailRepository = reservationDetailRepository;
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
}
