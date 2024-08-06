package com.hb.concert.domain.concert;

import com.hb.concert.domain.concert.converter.SeatIdConverter;
import com.hb.concert.domain.common.enumerate.UseYn;
import com.hb.concert.domain.common.enumerate.ValidState;
import com.hb.concert.support.CommonUtil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor @AllArgsConstructor
@Builder @Data
@Entity @Table(name = "HB_CONCERT_RESERVATION",indexes = {
        @Index(name = "idx_concert_reservation", columnList = "concertDetailId, reservationTime")
})
public class ConcertReservation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private UUID userId;
    private String reservationId;
    private String concertDetailId;
    @Convert(converter = SeatIdConverter.class)
    private List<Integer> reservedSeatId;
    private LocalDateTime reservationTime;
    @Enumerated(EnumType.STRING)
    private UseYn isPaid;
    @Enumerated(EnumType.STRING)
    private ValidState validState;

    @PrePersist protected void onCreate() {
        this.reservationTime = LocalDateTime.now();
        this.isPaid = UseYn.N;
        this.validState = ValidState.VALID;
        this.reservationId = new StringBuilder()
                .append("reserve_")
                .append(CommonUtil.dateToString(LocalDateTime.now(), CommonUtil.DateFormatType.DATE_TO_STRING_YYYYMMDDHHMMSS))
                .toString();
    }

    public void successPaid() {
        this.isPaid = UseYn.Y;
        this.reservationTime = LocalDateTime.now();
    }

    public void failPaid() {
        this.validState = ValidState.INVALID;
    }

    public void createReservation(UUID userId, String concertDetailId, List<Integer> seatId) {
        this.userId = userId;
        this.concertDetailId = concertDetailId;
        this.reservedSeatId = seatId;
    }
}