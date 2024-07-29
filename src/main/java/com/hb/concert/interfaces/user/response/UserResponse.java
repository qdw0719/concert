package com.hb.concert.interfaces.user.response;

import com.hb.concert.domain.user.User;

import java.util.UUID;

public record UserResponse(UUID userId, int balance) {
    public static UserResponse of(User user) {
        return new UserResponse(user.getUserId(), user.getBalance());
    }
}
