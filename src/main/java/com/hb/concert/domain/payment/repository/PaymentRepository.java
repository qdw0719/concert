package com.hb.concert.domain.payment.repository;

import com.hb.concert.domain.payment.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository {
    Optional<Payment> getPaymetInfoByReservationId(String reservationId);

    Payment save(Payment payment);

    void saveAll(List<Payment> paymentList);
}
