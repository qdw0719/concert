package com.hb.concert.concert.infra;

import com.hb.concert.concert.entity.Concert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConcertJpaRepository extends JpaRepository<Concert, Long> {
    Optional<Concert> findByConcertId(String concertId);
}