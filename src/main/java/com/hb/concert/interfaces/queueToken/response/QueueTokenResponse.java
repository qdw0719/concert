package com.hb.concert.interfaces.queueToken.response;

import com.hb.concert.domain.queueToken.ViewData.TokenInfo;

public record QueueTokenResponse(String token, int position) {
    public static QueueTokenResponse tokenInfoOf(TokenInfo tokenInfo) {
        return new QueueTokenResponse(tokenInfo.token(), tokenInfo.position());
    }
}
