package com.hb.concert.infrastructure.concert;

import com.hb.concert.domain.common.enumerate.UseYn;
import com.hb.concert.domain.concert.ConcertSeat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConcertSeatJpaRepository extends JpaRepository<ConcertSeat, Long>  {

    List<ConcertSeat> findByConcertIdAndUseYn(String concertId, UseYn useYn);

    List<ConcertSeat> findByConcertIdAndConcertDetailId(String concertId, String concertDetailId);
}
