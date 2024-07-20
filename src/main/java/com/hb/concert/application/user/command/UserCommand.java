package com.hb.concert.application.user.command;

import java.util.UUID;

public class UserCommand {

    public record SetUserBalance(UUID userId, int amount) {}
}
