package com.hb.concert.payment.entity.repository;

import com.hb.concert.payment.entity.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository {
    Optional<Payment> getPaymetInfoByReservationId(String reservationId);

    Payment save(Payment payment);

    void saveAll(List<Payment> paymentList);
}
