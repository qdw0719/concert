package com.hb.concert.queueToken.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@NoArgsConstructor @AllArgsConstructor
@Builder @Data
@Entity @Table(name = "HB_QUEUE_TOKEN")
public class QueueToken {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    private String concertDetailId;
    private Integer position;
    @Enumerated(EnumType.STRING)
    private TokenStatus status;
    private LocalDateTime expiredAt;

    @PrePersist protected void onCreate() {
        updateExpiredAt();
    }

    public void generateToken(String token, String concertDetailId, Integer position, TokenStatus status) {
        this.token = token;
        this.concertDetailId = concertDetailId;
        this.position = position;
        this.status = status;
    }

    public boolean isTokenExpiredTarget() {
        return this.expiredAt.plusMinutes(QueueTokenConfig.EXPRIED_AT_MINUTES).isBefore(LocalDateTime.now());
    }

    public void setTokenStatusWait() {
        this.status = TokenStatus.WAIT;
    }

    public void setTokenStatusProcess() {
        this.status = TokenStatus.PROCESS;
    }

    public void setTokenStatusExpired() {
        this.status = TokenStatus.EXPIRED;
    }

    public void updateExpiredAt() {
        this.expiredAt = LocalDateTime.now().plusMinutes(QueueTokenConfig.EXPRIED_AT_MINUTES);
    }

    public void reducePosition() {
        if (this.position > 0) {
            this.position--;
        }
    }
}
