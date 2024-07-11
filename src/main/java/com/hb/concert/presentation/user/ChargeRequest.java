package com.hb.concert.presentation.user;

import java.util.UUID;

public record ChargeRequest(
    UUID userId, int amount
) {
}
