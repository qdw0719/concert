package com.hb.concert.application.user;

import java.util.UUID;

public class UserCommand {
    public record Balance(UUID userId, int amount) {}
}
