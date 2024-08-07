package com.hb.concert.infrastructure.payment;

import com.hb.concert.domain.payment.Payment;
import com.hb.concert.domain.payment.repository.PaymentRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;

    public PaymentRepositoryImpl(PaymentJpaRepository paymentJpaRepository) {
        this.paymentJpaRepository = paymentJpaRepository;
    }

    @Override public Optional<Payment> getPaymetInfoByReservationId(String reservationId) {
        return paymentJpaRepository.findByReservationId(reservationId);
    }

    @Override public Payment save(Payment payment) {
        return paymentJpaRepository.save(payment);
    }

    @Override
    public void saveAll(List<Payment> paymentList) {
        paymentJpaRepository.saveAll(paymentList);
    }
}
