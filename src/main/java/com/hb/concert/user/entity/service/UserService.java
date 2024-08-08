package com.hb.concert.user.entity.service;

import com.hb.concert.exception.CustomException.NotFoundException;
import com.hb.concert.user.entity.User;
import com.hb.concert.user.entity.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service @Slf4j
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 유저 잔액충전 메서드
     *
     * @param userId
     * @param amount
     * @return
     */
    @Transactional
    public User charge(UUID userId, int amount) {
        User user = userRepository.getUserInfo(userId).orElseThrow(() -> new NotFoundException(NotFoundException.USER_NOT_FOUND));
        user.charge(amount);
        userRepository.save(user);
        return user;
    }

    /**
     * 유저 잔액차감 메서드
     *
     * @param userId
     * @param amount
     * @return
     */
    @Transactional
    public User consume(UUID userId, int amount) {
        User user = userRepository.getUserInfo(userId).orElseThrow(() -> new NotFoundException(NotFoundException.USER_NOT_FOUND));
        user.consume(amount);
        userRepository.save(user);
        return user;
    }
}