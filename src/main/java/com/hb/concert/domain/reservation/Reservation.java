package com.hb.concert.domain.reservation;

import com.hb.concert.domain.common.enumerate.UseYn;
import com.hb.concert.domain.common.enumerate.ValidState;
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
        name = "HB_RESERVATION",
        indexes = @Index(name = "idx_hb_reservation_reservationId", columnList = "reservationId")
)
public class Reservation {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("예약번호") @Column(nullable = false, unique = true)
    private String reservationId;

    @Comment("유저 UUID") @Column(nullable = false)
    private UUID userId;

    @Comment("콘서트 ID")
    private String concertId;

    @Comment("콘서트 detail ID") @Column(nullable = false)
    private String concertDetailId;

    @Comment("결제번호")
    private String paymentId;

    @Comment("결제 여부") @Enumerated(EnumType.STRING)
    private UseYn isPaid;

    @Comment("예약 요청 시간")
    private LocalDateTime reservationTime;

    @Comment("임시 예약 시간")
    private LocalDateTime temporaryGrantTime;

    @Comment("예약 확정 여부") @Enumerated(EnumType.STRING)
    private ValidState validState;
}
