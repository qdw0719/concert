package com.hb.concert.domain.payment.service;

import com.hb.concert.application.payment.command.PaymentCommand;
import com.hb.concert.common.exception.CustomException;
import com.hb.concert.common.exception.ExceptionMessage;
import com.hb.concert.domain.common.enumerate.UseYn;
import com.hb.concert.domain.payment.Payment;
import com.hb.concert.domain.payment.PaymentRepository;
import com.hb.concert.domain.queue.QueueToken;
import com.hb.concert.domain.queue.QueueTokenRepository;
import com.hb.concert.domain.queue.QueueToken.TokenStatus;
import com.hb.concert.domain.reservation.Reservation;
import com.hb.concert.domain.reservation.ReservationRepository;
import com.hb.concert.domain.user.User;
import com.hb.concert.domain.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final QueueTokenRepository queueTokenRepository;

    public PaymentService(PaymentRepository paymentRepository, UserRepository userRepository,
                          ReservationRepository reservationRepository, QueueTokenRepository queueTokenRepository) {
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.reservationRepository = reservationRepository;
        this.queueTokenRepository = queueTokenRepository;
    }

    /**
     * 결제 생성
     *
     * @param command 결제 생성
     * @return Payment 결제
     */
    @Transactional
    public Payment createPayment(PaymentCommand.CreatePayment command) {
        User user = userRepository.findByUserId(command.userId())
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        if (user.getBalance() < command.totalPrice()) {
            throw new IllegalArgumentException("잔액이 부족합니다. 충전 후 이용해주세요.");
        }

        user.setBalance(user.getBalance() - command.totalPrice());
        userRepository.save(user);

        Payment payment = Payment.builder()
                .paymentId(UUID.randomUUID().toString())
                .userId(command.userId())
                .amount(command.amount())
                .totalPrice(command.totalPrice())
                .regTime(LocalDateTime.now())
                .build();
        paymentRepository.save(payment);

        if (reservationRepository.countByReservationId(command.reservationId()) == 0) {
            new CustomException.NotFoundException(ExceptionMessage.NOT_FOUND.replace("{msg}", "해당하는 예약 건"));
        }

        Reservation reservation = reservationRepository.findByReservationId(command.reservationId());
        reservation.setIsPaid(UseYn.Y);
        reservationRepository.save(reservation);

        QueueToken token = queueTokenRepository.findByToken(command.token());
        token.setStatus(TokenStatus.EXPIRED);
        queueTokenRepository.save(token);

        return payment;
    }
}
