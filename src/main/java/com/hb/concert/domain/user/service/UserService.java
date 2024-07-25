package com.hb.concert.domain.user.service;

import com.hb.concert.application.user.command.UserCommand;
import com.hb.concert.domain.exception.CustomException;
import com.hb.concert.domain.user.User;
import com.hb.concert.domain.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service @Slf4j
public class UserService {

    private final UserRepository userRepository;
//    private final RedisTemplate<String, Object> redisTemplate;

    public UserService(
            UserRepository userRepository
//            ,RedisTemplate<String, Object> redisTemplate
    ) {
        this.userRepository = userRepository;
//        this.redisTemplate = redisTemplate;
    }

//    낙관락 사용버전
    /**
     * 유저의 잔액을 충전하는 메소드
     *
     * @param command 유저의 UUID, 충전할 금액
     */
    @Transactional
    public void chargeBalance(UserCommand.SetUserBalance command) {
        long startTime = System.currentTimeMillis();

        try {
            User user = userRepository.findByUserId(command.userId())
                    .orElseThrow(() -> new CustomException.NotFoundException(CustomException.NotFoundException.USER_NOT_FOUND));

            user.setBalance(user.getBalance() + command.amount());
            saveUser(user);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw e;
        }

        long endTime = System.currentTimeMillis();
        log.info("총 걸린 시간 >>> {} ms", endTime - startTime);
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
        long startTime = System.currentTimeMillis();

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException.NotFoundException(CustomException.NotFoundException.USER_NOT_FOUND));

        if (user.getBalance() < amount) {
            throw new CustomException.BadRequestException(CustomException.BadRequestException.PAYMENT_NOT_ENOUGH_AMOUNT);
        }

        user.setBalance(user.getBalance() - amount);
        saveUser(user);

        long endTime = System.currentTimeMillis();
        log.info("총 걸린 시간 >>> {} ms", endTime - startTime);
    }


//    redis 사용버전
//
//
//    @Transactional
//    public void chargeBalance(UserCommand.SetUserBalance command) {
//        String lockKey = "userBalanceLock:" + command.userId();
//        Boolean isLocked = redisTemplate.opsForValue().setIfAbsent(lockKey, "locked", 5, TimeUnit.SECONDS);
//
//        if (Boolean.TRUE.equals(isLocked)) {
//            try {
//                User user = userRepository.findByUserId(command.userId())
//                        .orElseThrow(() -> new CustomException.NotFoundException(CustomException.NotFoundException.USER_NOT_FOUND));
//                user.setBalance(user.getBalance() - command.amount());
//                saveUser(user);
//            } catch (Exception e) {
//                log.info("Error while deducting balance: {}", e.getMessage());
//                throw e;
//            } finally {
//                releaseLock(lockKey);
//            }
//        } else {
//            log.warn("Unable to acquire lock for user balance: {}", command.userId());
//            throw new CustomException.InvalidServerException(CustomException.InvalidServerException.NOT_DEDUCT_BALANCE);
//        }
//    }
//
//
//    @Transactional
//    public void deductBalance(UUID userId, int amount) {
//        String lockKey = "userBalanceLock:" + userId;
//        Boolean isLocked = redisTemplate.opsForValue().setIfAbsent(lockKey, "locked", 5, TimeUnit.SECONDS);
//
//        if (Boolean.TRUE.equals(isLocked)) {
//            try {
//                User user = userRepository.findByUserId(userId)
//                        .orElseThrow(() -> new CustomException.NotFoundException(CustomException.NotFoundException.USER_NOT_FOUND));
//
//                if (user.getBalance() < amount) {
//                    throw new CustomException.BadRequestException(CustomException.BadRequestException.PAYMENT_NOT_ENOUGH_AMOUNT);
//                }
//
//                user.setBalance(user.getBalance() - amount);
//                saveUser(user);
//            } catch (Exception e) {
//                log.info("Error while deducting balance: {}", e.getMessage());
//                throw e;
//            } finally {
//                releaseLock(lockKey);
//            }
//        } else {
//            log.warn("Unable to acquire lock for user balance: {}", userId);
//            throw new CustomException.InvalidServerException(CustomException.InvalidServerException.NOT_DEDUCT_BALANCE);
//        }
//    }
//
//    private void releaseLock(String lockKey) {
//        boolean released = redisTemplate.delete(lockKey);
//        if (!released) {
//            log.warn("Failed to release lock for key: {}", lockKey);
//        }
//    }

    @Transactional
    public void saveUser(User user) {
        userRepository.save(user);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
}
