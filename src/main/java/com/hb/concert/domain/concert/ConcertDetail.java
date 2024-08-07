package com.hb.concert.domain.concert;

import com.hb.concert.domain.exception.CustomException.BadRequestException;
import com.hb.concert.support.CommonUtil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor @AllArgsConstructor
@Builder @Data
@Entity @Table(name = "HB_CONCERT_DETAIL"
        , indexes = { @Index(name = "idx_concert_detail", columnList = "concertDate") }
)
public class ConcertDetail {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String concertDetailId;
    private String concertId;
    private LocalDate concertDate;
    private Integer capacity;
    private Integer availableSeatCount;

    @PrePersist protected void onCreate() {
        // capacity 최대 100개
        if (CommonUtil.isNull(this.capacity) || this.capacity > 100) {
            this.capacity = 100;
        }
        if (CommonUtil.isNull(this.availableSeatCount) || this.availableSeatCount == 0 || this.availableSeatCount > 100) {
            this.availableSeatCount = this.capacity;
        }
    }

    @PreUpdate protected void onUpdate() {
        if (this.availableSeatCount == 0) {
            throw new BadRequestException(BadRequestException.SEAT_SOLDOUT_IN_CONCERT);
        }
    }

    public void reduceAvailableSeat(int amount) {
        if (this.availableSeatCount - amount < 0) {
            throw new BadRequestException(BadRequestException.ANY_SEAT_RESERVE_ALREADY);
        }
        this.availableSeatCount -= amount;
    }
}
