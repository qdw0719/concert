package com.hb.concert.payment.infra;

import com.hb.concert.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {

    @Query("select p from Payment p where p.paidTime is null and p.reservationId = :reservationId")
    Optional<Payment> findByReservationId(String reservationId);
}
