package com.hb.concert.application.concert;

import com.hb.concert.application.concert.command.ConcertCommand;
import com.hb.concert.application.concert.facade.ConcertFacade;
import com.hb.concert.application.payment.command.PaymentCommand;
import com.hb.concert.application.payment.facade.PaymentFacade;
import com.hb.concert.application.reservation.command.ReservationCommand;
import com.hb.concert.application.reservation.facade.ReservationFacade;
import com.hb.concert.application.reservation.service.ReservationDetailService;
import com.hb.concert.application.reservation.service.ReservationService;
import com.hb.concert.common.exception.NotFoundException;
import com.hb.concert.config.util.JwtUtil;
import com.hb.concert.domain.common.enumerate.UseYn;
import com.hb.concert.domain.concert.*;
import com.hb.concert.domain.payment.Payment;
import com.hb.concert.domain.queue.QueueToken;
import com.hb.concert.domain.queue.QueueTokenRepository;
import com.hb.concert.domain.reservation.Reservation;
import com.hb.concert.domain.user.User;
import com.hb.concert.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest @Transactional
class ConcertIntegrationTest {

    @Autowired
    private ConcertFacade concertFacade;

    @Autowired
    private ReservationFacade reservationFacade;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationDetailService reservationDetailService;

    @Autowired
    private PaymentFacade paymentFacade;

    @Autowired
    private QueueTokenRepository queueTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private ConcertDetailRepository concertDetailRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private UUID userId;
    private String concertId;
    private String detailId;
    private String token;

    @BeforeEach
    void setUp() {
        Optional<User> userOptional = userRepository.findById(1L);
        User user = userOptional.orElseThrow(() -> new NotFoundException("User not found")); // Handling user not found
        userId = user.getUserId();

        concertId = concertRepository.findAll().get(0).getConcertId();
        detailId = concertDetailRepository.findByConcertId(concertId).getConcertDetailId();

        token = jwtUtil.generateToken(userId, 0, 0);

        if (token == null) {
            throw new RuntimeException("Generated token is null");
        }

        QueueToken queueToken = QueueToken.builder()
                .token(token)
                .userId(userId)
                .position(0)
                .waitTime(0)
                .isActive(UseYn.Y)
                .status(QueueToken.TokenStatus.WAIT)
                .build();
        queueTokenRepository.save(queueToken);
    }

    @Test
    void 토큰발급과_검증() {
        String generatedToken = jwtUtil.generateToken(userId, 0, 0);
        assertThat(generatedToken).isNotNull();

        UUID validatedUserId = jwtUtil.getUserIdFromToken(generatedToken);
        assertThat(validatedUserId).isEqualTo(userId);

        assertDoesNotThrow(() -> jwtUtil.validateToken(generatedToken));
    }

    @Test
    void 예약가능한_콘서트_조회() {
        List<Concert> availableConcerts = concertFacade.getAvailableConcerts();
        assertFalse(availableConcerts.isEmpty());
    }

    @Test
    void 예약가능한_상세_정보조회() {
        ConcertCommand.GetAvailableDetails command = new ConcertCommand.GetAvailableDetails(userId, concertId, LocalDate.now(), token);
        List<ConcertDetail> availableDetails = concertFacade.getAvailableDetails(command);
        assertFalse(availableDetails.isEmpty());
    }

    @Test
    void 유효하지_않은_토큰으로_상세_정보조회() {
        String invalidToken = jwtUtil.generateToken(UUID.randomUUID(), 1, 5);
        ConcertCommand.GetAvailableDetails command = new ConcertCommand.GetAvailableDetails(userId, concertId, LocalDate.now(), invalidToken);

        assertThrows(IllegalArgumentException.class, () -> {
            concertFacade.getAvailableDetails(command);
        });
    }

    @Test
    void 대기열순번이_0이아닌_상황에서_상세_정보조회() {
        String queueTokenStr = jwtUtil.generateToken(userId, 1, 5);
        QueueToken queueToken = QueueToken.builder()
                .token(queueTokenStr)
                .userId(userId)
                .position(1)
                .waitTime(5)
                .isActive(UseYn.Y)
                .status(QueueToken.TokenStatus.WAIT)
                .build();
        queueTokenRepository.save(queueToken);

        ConcertCommand.GetAvailableDetails command = new ConcertCommand.GetAvailableDetails(userId, concertId, LocalDate.now(), queueTokenStr);

        assertThrows(IllegalArgumentException.class, () -> {
            concertFacade.getAvailableDetails(command);
        });
    }

    @Test
    void 콘서트좌석_조회() {
        ConcertCommand.getConcertSeat command = new ConcertCommand.getConcertSeat(concertId, detailId);
        List<ConcertSeat> concertSeats = concertFacade.getConcertSeat(command);
        assertFalse(concertSeats.isEmpty());
    }

    @Test
    void 결제_생성() {
        ReservationCommand.Create reservationCommand = new ReservationCommand.Create(userId, concertId, detailId, List.of(1));
        Reservation reservation = reservationService.createReservation(reservationCommand);

        PaymentCommand.CreatePayment paymentCommand = new PaymentCommand.CreatePayment(userId, 1, 30000, reservation.getReservationId(), token);
        Payment payment = paymentFacade.createPayment(paymentCommand);
        assertThat(payment).isNotNull();
    }

    @Test
    void 잔액부족으로인한_결제실패() {
        User user = userRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("User not found")); // Handling user not found
        user.setBalance(1000);
        userRepository.save(user);

        ReservationCommand.Create reservationCommand = new ReservationCommand.Create(userId, concertId, detailId, List.of(1));
        Reservation reservation = reservationService.createReservation(reservationCommand);

        PaymentCommand.CreatePayment paymentCommand = new PaymentCommand.CreatePayment(userId, 1, 30000, reservation.getReservationId(), token);

        assertThrows(IllegalArgumentException.class, () -> {
            paymentFacade.createPayment(paymentCommand);
        });
    }

    @Test
    void 중복예약_시도() {
        ReservationCommand.Create reservationCommand = new ReservationCommand.Create(userId, concertId, detailId, List.of(1, 2, 3, 4));
        reservationService.createReservation(reservationCommand);

        assertThrows(IllegalArgumentException.class, () -> {
            reservationService.createReservation(reservationCommand);
        });
    }

    @Test
    void 만료된_예약처리() {
        ReservationCommand.Create reservationCommand = new ReservationCommand.Create(userId, concertId, detailId, List.of(1));
        Reservation reservation = reservationService.createReservation(reservationCommand);
        reservation.setTemporaryGrantTime(LocalDateTime.now().minusMinutes(10));
        reservationFacade.expireReservations();

        List<Integer> seatIds = reservationDetailService.getConcertSeatIdByReservationId(reservation.getReservationId());
        for (Integer seatId : seatIds) {
            ConcertSeat seat = concertFacade.getConcertSeat(new ConcertCommand.getConcertSeat(concertId, detailId))
                    .stream().filter(s -> s.getConcertSeatId().equals(seatId)).findFirst().orElse(null);
            assertThat(seat).isNotNull();
            assertThat(seat.getUseYn()).isEqualTo(UseYn.Y);
        }
    }
}
