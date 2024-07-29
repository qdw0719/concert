package com.hb.concert.interfaces.user.request;

import com.hb.concert.application.user.UserCommand;

import java.util.UUID;

public record UserRequest(UUID userId, int amount) {
    public UserCommand.Balance toBalanceCommand() {
        return new UserCommand.Balance(userId, amount);
    }
}
