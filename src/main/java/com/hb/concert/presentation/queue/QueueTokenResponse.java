package com.hb.concert.presentation.queue;

import com.hb.concert.domain.queue.QueueToken;

public record QueueTokenResponse(
        String token
) {
    public static QueueTokenResponse of(QueueToken queuetoken) {
        return new QueueTokenResponse(
                queuetoken.getToken()
        );
    }
}
