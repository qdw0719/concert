package com.hb.concert.application.concert;

import java.util.List;
import java.util.UUID;

public class ConcertCommand {
    public record Search(String concertId, String concertDetailId) {}
    public record CreateReservation(UUID userId, String concertDetailId, List<Integer> seatId) {}
}
