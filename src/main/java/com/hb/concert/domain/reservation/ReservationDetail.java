package com.hb.concert.domain.reservation;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@NoArgsConstructor @AllArgsConstructor
@Data @Builder
@Entity
@Table(
        name = "HB_RESERVATION_DETAIL",
        indexes = {
            @Index(name = "idx_hb_reservation_detail_reservationId", columnList = "reservationId"),
            @Index(name = "idx_hb_reservation_detail_concertSeatId", columnList = "concertSeatId")
        }
)
public class ReservationDetail {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("예약번호") @Column(nullable = false)
    private String reservationId;

    @Comment("예약 좌석 번호")
    private Integer concertSeatId;
}
