package com.hb.concert.domain.queueToken;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class QueueTokenRedis implements Serializable {
    private String token;
    private String concertDetailId;
    private int position;
    private TokenStatus status;
    private LocalDateTime expiredAt;

    public QueueTokenRedis(String token, String concertDetailId, int position, TokenStatus status) {
        this.token = token;
        this.concertDetailId = concertDetailId;
        this.position = position;
        this.status = status;
        this.expiredAt = LocalDateTime.now().plusMinutes(5);
    }

    public void updateExpiredAt() {
        this.expiredAt = LocalDateTime.now().plusMinutes(5);
    }

    public void setTokenStatusExpired() {
        this.status = TokenStatus.EXPIRED;
    }

    public void setTokenStatusProcess() {
        this.status = TokenStatus.PROCESS;
    }

    public void reducePosition() {
        this.position--;
    }

    public boolean isTokenExpiredTarget() {
        return LocalDateTime.now().isAfter(expiredAt);
    }

    public void setPosition(int position) {
        this.position = position;
    }
}