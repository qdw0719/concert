package com.hb.concert.user.interfaces.request;

import com.hb.concert.user.application.UserCommand;

import java.util.UUID;

public record UserRequest(UUID userId, int amount) {
    public UserCommand.Balance toBalanceCommand() {
        return new UserCommand.Balance(userId, amount);
    }
}
