package com.hb.concert.domain.concert;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@NoArgsConstructor @AllArgsConstructor
@Data @Builder
@Entity @Table(name = "HB_CONCERT")
public class Concert {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("콘서트 ID, 임의채번") @Column(nullable = false, unique = true)
    private String concertId;

    @Comment("콘서트 명")
    private String concertName;

    @Comment("아티스트 명")
    private String artist;
}
