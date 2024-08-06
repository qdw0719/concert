package com.hb.concert.domain.payment;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor @AllArgsConstructor
@Builder @Data
@Entity @Table(name = "HB_PAYMENT",indexes = {
        @Index(name = "idx_payment", columnList = "reservationId")
})
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String reservationId;
    private LocalDateTime effectiveTime;
    private LocalDateTime paidTime;

    public void createPayment(String reservationId) {
        this.reservationId = reservationId;
        this.effectiveTime = LocalDateTime.now().plusMinutes(5);
    }

    public void successPayment() {
        paidTime = LocalDateTime.now();
    }
}
