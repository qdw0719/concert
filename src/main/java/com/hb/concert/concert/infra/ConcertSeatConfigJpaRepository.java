package com.hb.concert.concert.infra;

import com.hb.concert.concert.entity.ConcertSeatConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConcertSeatConfigJpaRepository extends JpaRepository<ConcertSeatConfig, Long> {
    List<ConcertSeatConfig> findByIdLessThanEqual(Integer capacity);
}
