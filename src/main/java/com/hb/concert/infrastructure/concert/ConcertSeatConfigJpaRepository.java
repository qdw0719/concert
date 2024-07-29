package com.hb.concert.infrastructure.concert;

import com.hb.concert.domain.concert.ConcertSeatConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConcertSeatConfigJpaRepository extends JpaRepository<ConcertSeatConfig, Long> {
    List<ConcertSeatConfig> findByIdLessThanEqual(Integer capacity);
}
