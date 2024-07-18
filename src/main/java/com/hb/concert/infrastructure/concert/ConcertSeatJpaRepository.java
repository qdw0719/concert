package com.hb.concert.infrastructure.concert;

import com.hb.concert.domain.common.enumerate.UseYn;
import com.hb.concert.domain.concert.ConcertSeat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ConcertSeatJpaRepository extends JpaRepository<ConcertSeat, Long>  {

    List<ConcertSeat> findByConcertIdAndUseYn(String concertId, UseYn useYn);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select cs from ConcertSeat cs where cs.concertId = :concertId and cs.concertDetailId = :concertDetailId")
    List<ConcertSeat> findByConcertIdAndConcertDetailId(String concertId, String concertDetailId);
}
