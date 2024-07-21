package com.hb.concert.domain.concert;

import com.hb.concert.domain.common.enumerate.UseYn;
import com.hb.concert.domain.exception.CustomException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@NoArgsConstructor @AllArgsConstructor
@Data @Builder
@Entity @Table(name = "HB_CONCERT_SEAT")
public class ConcertSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("좌석 번호") @Column(nullable = false)
    private Integer concertSeatId;

    @Comment("콘서트 ID")
    private String concertId;

    @Comment("콘서트 detail ID")
    private String concertDetailId;

    @Comment("좌석 가격")
    private Integer price;

    @Comment("좌석 예약 가능 여부") @Enumerated(EnumType.STRING)
    private UseYn useYn;

    public void reserved() {
        if (this.useYn == UseYn.N) {
            throw new CustomException.BadRequestException(CustomException.BadRequestException.ALREADY_RESERVED);
        }
        this.useYn = UseYn.N;
    }

    public void release() {
        this.useYn = UseYn.Y;
    }
}