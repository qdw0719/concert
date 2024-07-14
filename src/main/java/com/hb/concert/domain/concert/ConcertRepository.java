package com.hb.concert.domain.concert;

import java.util.List;

public interface ConcertRepository {
    List<Concert> findAll();
    Concert findByConcertId(String concertId);

    List<Concert> saveAll(List<Concert> concertList);

    int count();

    int countByConcertId(String concertId);
}
