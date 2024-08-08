package com.hb.concert.user.application;

import java.util.UUID;

public class UserCommand {
    public record Balance(UUID userId, int amount) {}
}
