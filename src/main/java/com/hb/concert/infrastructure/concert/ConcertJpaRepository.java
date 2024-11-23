package com.hb.concert.infrastructure.concert;

import com.hb.concert.domain.concert.Concert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConcertJpaRepository extends JpaRepository<Concert, Long> {
    Concert findByConcertId(String concertId);
    List<Concert> findAll();
}
