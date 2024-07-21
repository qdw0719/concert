package com.hb.concert.domain.concert.service;

import com.hb.concert.application.concert.command.ConcertCommand;
import com.hb.concert.domain.common.enumerate.UseYn;
import com.hb.concert.domain.common.enumerate.ValidState;
import com.hb.concert.domain.concert.*;
import com.hb.concert.domain.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service @Slf4j
public class ConcertService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ConcertRepository concertRepository;
    private final ConcertDetailRepository concertDetailRepository;
    private final ConcertSeatRepository concertSeatRepository;

    public ConcertService(RedisTemplate<String, Object> redisTemplate, ConcertRepository concertRepository, ConcertDetailRepository concertDetailRepository, ConcertSeatRepository concertSeatRepository) {
        this.redisTemplate = redisTemplate;
        this.concertRepository = concertRepository;
        this.concertDetailRepository = concertDetailRepository;
        this.concertSeatRepository = concertSeatRepository;
    }

    /**
     * 특정 콘서트를 ID로 조회
     *
     * @param concertId 콘서트 ID
     * @return 콘서트
     */
    public Concert findByConcertId(String concertId) {
        return concertRepository.findByConcertId(concertId);
    }

    /**
     * 특정 콘서트의 예약 가능한 상세 정보 목록을 조회
     *
     * @param concertId 콘서트 ID
     * @param currentDate 현재 날짜
     * @return 예약 가능한 콘서트 상세 정보 목록
     */
    public List<ConcertDetail> findAvailableDetails(String concertId, LocalDate currentDate) {
        return concertDetailRepository.findByConcertIdAndConcertDateAfterAndValidState(concertId, currentDate, ValidState.VALID);
    }

    /**
     * 현재 날짜 이후 예약 가능한 모든 콘서트 ID 목록을 조회
     *
     * @param currentDate 현재 날짜
     * @return 예약 가능한 콘서트 ID 목록
     */
    public List<Concert> getAvailableConcerts(LocalDate currentDate) {
        List<String> concertIdList = concertDetailRepository.findDistinctConcertIdByConcertDateAfter(currentDate);
        return concertIdList.stream()
                .map(concertRepository::findByConcertId)
                .collect(Collectors.toList());
    }

    /**
     * 특정 콘서트의 예약 가능한 좌석 목록을 조회
     *
     * @param command  콘서트 ID, detailId 일정ID
     * @return 예약 가능한 콘서트 좌석 목록
     */
    public List<ConcertSeat> findConcertSeats(ConcertCommand.GetConcertSeat command) {
        return concertSeatRepository.findByConcertIdAndConcertDetailId(command.concertId(), command.detailId());
    }

    /**
     * 콘서트 좌석예약
     *
     * @param concertId
     * @param concertDetailId
     * @param concertSeatIdList
     * @return ConcertSeat
     */
    @Transactional
    public void saveConcertSeat(String concertId, String concertDetailId, List<Integer> concertSeatIdList) {
        List<ConcertSeat> saveTargetSeat = new ArrayList<>();

        concertSeatIdList.stream().map(seatId -> {
            String lockKey = getLockKey(concertId, seatId);
            Boolean isLocked = redisTemplate.opsForValue().setIfAbsent(lockKey, "locked", 5, TimeUnit.SECONDS);
            if (Boolean.TRUE.equals(isLocked)) {
                try {
                    ConcertSeat concertSeat = concertSeatRepository.findByConcertIdAndConcertDetailIdAndConcertSeatId(concertId, concertDetailId, seatId);
                    concertSeat.reserved();
                    saveTargetSeat.add(concertSeat);
                } catch (Exception e) {
                    log.info("Error while saving concert seat: {}", e.getMessage());
                    throw e;
                } finally {
                    redisTemplate.delete(lockKey);
                }
            } else {
                log.warn("Unable to acquire lock for seat: {}", seatId);
                throw new CustomException.InvalidServerException(CustomException.InvalidServerException.NOT_SELECTED_SEAT);
            }
            return null;
        });
        concertSeatRepository.saveAll(saveTargetSeat);
    }

    private String getLockKey(String concertId, Integer concertSeatId) {
        return new StringBuilder()
                .append("ConcertSeatLock:")
                .append(concertId)
                .append(":")
                .append(concertSeatId)
                .toString();
    }

    /**
     * concertId validation
     * @param concertId
     * @return boolean
     */
    public boolean isConcertCountNotFound(String concertId) {
        return concertRepository.countByConcertId(concertId) == 0;
    }

    /**
     * detailId validation
     * @param concertId
     * @param detailId
     * @return boolean
     */
    public boolean isConcertDetailCountNotFound(String concertId, String detailId) {
        return concertDetailRepository.countByConcertIdAndConcertDetailId(concertId, detailId) == 0;
    }
}
