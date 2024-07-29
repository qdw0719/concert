package com.hb.concert.domain.payment.repository;

import com.hb.concert.domain.payment.Payment;

import java.util.Optional;

public interface PaymentRepository {
    Optional<Payment> getPaymetInfoByReservationId(String reservationId);

    Payment save(Payment payment);
}
