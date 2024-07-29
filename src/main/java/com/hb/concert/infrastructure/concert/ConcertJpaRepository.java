package com.hb.concert.infrastructure.concert;

import com.hb.concert.domain.concert.Concert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConcertJpaRepository extends JpaRepository<Concert, Long> {
    Optional<Concert> findByConcertId(String concertId);
}