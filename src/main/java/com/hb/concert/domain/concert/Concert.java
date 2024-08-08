package com.hb.concert.domain.concert;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor @AllArgsConstructor
@Builder @Data
@Entity @Table(name = "HB_CONCERT")
public class Concert {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String concertId;
    private String concertName;
    private String artist;
}
