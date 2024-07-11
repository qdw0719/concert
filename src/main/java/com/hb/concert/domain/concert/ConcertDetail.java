package com.hb.concert.domain.concert;

import com.hb.concert.domain.common.enumerate.ValidState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;

@NoArgsConstructor @AllArgsConstructor
@Data @Builder
@Entity @Table(name = "HB_CONCERT_DETAIL")
public class ConcertDetail {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("콘서트 detail ID") @Column(nullable = false, unique = true)
    private String concertDetailId;

    @Comment("콘서트 ID") @Column(nullable = false)
    private String concertId;

    @Comment("콘서트 날짜")
    private LocalDate concertDate;

    @Comment("콘서트 장소")
    private String location;

    @Comment("만석 혹은 예약기간 종료") @Enumerated(EnumType.STRING)
    private ValidState validState;
}
