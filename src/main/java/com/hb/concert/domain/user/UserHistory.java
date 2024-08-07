package com.hb.concert.domain.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor @AllArgsConstructor
@Builder @Data
@Entity @Table(name = "HB_USER_HISOTY"
        , indexes = { @Index(name = "idx_user_history", columnList = "createAt") }
)
public class UserHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private UUID userId;
    private Integer amount;
    private Integer balance;
    private LocalDateTime createAt;
}
