package com.hb.concert.domain.payment;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor @AllArgsConstructor
@Data @Builder
@Entity
@Table(
        name = "HB_PAYMENT",
        indexes = {
                @Index(name = "idx_hb_payment_paymentId", columnList = "paymentId"),
                @Index(name = "idx_hb_payment_regTime", columnList = "regTime")
        }
)
public class Payment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("결제 번호") @Column(nullable = false, unique = true)
    private String paymentId;

    @Comment("유저 UUID") @Column(nullable = false)
    private UUID userId;

    @Comment("구매 수량")
    private Integer amount;

    @Comment("총 결제 금액")
    private Integer totalPrice;

    @Comment("결제 시간")
    private LocalDateTime regTime;
}
