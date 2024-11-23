package com.hb.concert.user.interfaces.response;

import com.hb.concert.user.entity.User;

import java.util.UUID;

public record UserResponse(UUID userId, int balance) {
    public static UserResponse of(User user) {
        return new UserResponse(user.getUserId(), user.getBalance());
    }
}
