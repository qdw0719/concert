package com.hb.concert.domain.user.service;

import com.hb.concert.application.user.command.UserCommand;
import com.hb.concert.domain.exception.CustomException;
import com.hb.concert.domain.exception.CustomException.NotFoundException;

import com.hb.concert.domain.user.User;
import com.hb.concert.domain.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service @Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public UserService(UserRepository userRepository, RedisTemplate<String, Object> redisTemplate) {
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 유저의 잔액을 충전하는 메소드
     *
     * @param command 유저의 UUID, 충전할 금액
     */
    @Transactional
    public void chargeBalance(UserCommand.SetUserBalance command) {
        String lockKey = "userBalanceLock:" + command.userId();
        Boolean isLocked = redisTemplate.opsForValue().setIfAbsent(lockKey, "locked", 5, TimeUnit.SECONDS);

        if (Boolean.TRUE.equals(isLocked)) {
            try {
                User user = userRepository.findByUserId(command.userId())
                        .orElseThrow(() -> new CustomException.NotFoundException(CustomException.NotFoundException.USER_NOT_FOUND));

                user.setBalance(user.getBalance() + command.amount());
                userRepository.save(user);
            } catch (Exception e) {
                log.error("Error while charging balance: {}", e.getMessage());
                throw e;
            } finally {
                log.warn("Unable to acquire lock for user balance: {}", command.userId());
                redisTemplate.delete(lockKey);
            }
        } else {
            throw new CustomException.BadRequestException("현재 잔액을 충전할 수 없습니다. 다시 시도해 주세요.");
        }
    }

    /**
     * 유저 포인트 차감하는 메서드
     *
     * @param userId
     * @param amount
     *
     */
    @Transactional
    public void deductBalance(UUID userId, int amount) {
        String lockKey = "userBalanceLock:" + userId;
        Boolean isLocked = redisTemplate.opsForValue().setIfAbsent(lockKey, "locked", 5, TimeUnit.SECONDS);

        if (Boolean.TRUE.equals(isLocked)) {
            try {
                User user = userRepository.findByUserId(userId)
                        .orElseThrow(() -> new CustomException.NotFoundException(CustomException.NotFoundException.USER_NOT_FOUND));

                if (user.getBalance() < amount) {
                    throw new CustomException.BadRequestException(CustomException.BadRequestException.PAYMENT_NOT_ENOUGH_AMOUNT);
                }

                user.setBalance(user.getBalance() - amount);
                userRepository.save(user);
            } catch (Exception e) {
                log.error("Error while deducting balance: {}", e.getMessage());
                throw e;
            } finally {
                redisTemplate.delete(lockKey);
            }
        } else {
            log.warn("Unable to acquire lock for user balance: {}", userId);
            throw new CustomException.InvalidServerException(CustomException.InvalidServerException.NOT_DEDUCT_BALANCE);
        }
    }

    @Transactional
    public void saveUser(User user) {
        userRepository.save(user);
    }
}
