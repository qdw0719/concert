package com.hb.concert.application.payment.facade;

import com.hb.concert.application.payment.command.PaymentCommand;
import com.hb.concert.support.config.util.JwtUtil;
import com.hb.concert.domain.common.enumerate.UseYn;
import com.hb.concert.domain.exception.CustomException;
import com.hb.concert.domain.reservation.Reservation;
import com.hb.concert.domain.reservation.service.ReservationService;
import com.hb.concert.domain.user.User;
import com.hb.concert.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PaymentFacadeTest {

    @Autowired PaymentFacade PaymentFacade;
    @Autowired ReservationService reservationService;
    @Autowired UserService userService;
    @Autowired JwtUtil jwtUtil;

    String token;
    UUID userId;

    Reservation requestReservationInfoValid;
    Reservation requestReservationInfoTimeout;

    @BeforeEach
    void setUp() {
        // given
        userId = UUID.randomUUID();
        token = new StringBuilder()
                .append("Bearer ")
                .append(jwtUtil.generateToken(userId, 0, 0))
                .toString();
        requestReservationInfoValid = new Reservation().builder()
                .id(1L)
                .reservationId("reservation_0001")
                .userId(userId)
                .concertId("concert1")
                .concertDetailId("detail1")
                .isPaid(UseYn.N)
                .reservationTime(LocalDateTime.now())
                .temporaryGrantTime(LocalDateTime.now().plusMinutes(5))
                .build();
        reservationService.saveReservation(requestReservationInfoValid);

        requestReservationInfoTimeout = new Reservation().builder()
                .id(1L)
                .reservationId("reservation_0001")
                .userId(userId)
                .concertId("concert1")
                .concertDetailId("detail1")
                .isPaid(UseYn.N)
                .reservationTime(LocalDateTime.now())
                .temporaryGrantTime(LocalDateTime.now().plusMinutes(10))
                .build();
        reservationService.saveReservation(requestReservationInfoTimeout);
    }

    @Test
    void 결제요청_예약번호가_없을_때_실패() {
        // given
        PaymentCommand.CreatePayment command = new PaymentCommand.CreatePayment(requestReservationInfoValid.getUserId(), 3, 52500, "reservation_1111", token);

        // when then
        assertThrows(CustomException.NotFoundException.class, () -> {
            PaymentFacade.createPayment(command);
        });
    }

    @Test
    void 결제요청_결제가능시간_초과했을_때_실패() {
        // given
        PaymentCommand.CreatePayment command = new PaymentCommand.CreatePayment(requestReservationInfoTimeout.getUserId(), 3, 52500, requestReservationInfoTimeout.getReservationId(), token);

        // when then
        assertThrows(CustomException.BadRequestException.class, () -> {
            PaymentFacade.createPayment(command);
        });
    }

    @Test
    void 유저가_잔액부족일_때_실패() {
        // given
        User user = new User().builder()
                .id(1L)
                .userId(userId)
                .balance(3000)
                .build();
        userService.saveUser(user);

        PaymentCommand.CreatePayment command = new PaymentCommand.CreatePayment(user.getUserId(), 3, 52500, requestReservationInfoTimeout.getReservationId(), token);

        // when then
        assertThrows(CustomException.BadRequestException.class, () -> {
            PaymentFacade.createPayment(command);
        });
    }
}