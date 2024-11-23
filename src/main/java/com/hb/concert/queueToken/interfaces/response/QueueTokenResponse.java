package com.hb.concert.queueToken.interfaces.response;

import com.hb.concert.queueToken.entity.ViewData.TokenInfo;

public record QueueTokenResponse(String token, int position) {
    public static QueueTokenResponse tokenInfoOf(TokenInfo tokenInfo) {
        return new QueueTokenResponse(tokenInfo.token(), tokenInfo.position());
    }
}
