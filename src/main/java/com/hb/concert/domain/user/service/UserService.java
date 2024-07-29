package com.hb.concert.domain.user.service;

import com.hb.concert.domain.exception.CustomException.NotFoundException;
import com.hb.concert.domain.user.User;
import com.hb.concert.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
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
    public User consume(UUID userId, int amount) {
        User user = userRepository.getUserInfo(userId).orElseThrow(() -> new NotFoundException(NotFoundException.USER_NOT_FOUND));
        user.consume(amount);
        userRepository.save(user);
        return user;
    }
}