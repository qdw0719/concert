package com.hb.concert.domain.user.service;

import com.hb.concert.application.user.command.UserCommand;
import com.hb.concert.domain.exception.CustomException.NotFoundException;

import com.hb.concert.domain.user.User;
import com.hb.concert.domain.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 유저의 잔액을 충전하는 메소드
     *
     * @param command 유저의 UUID, 충전할 금액
     */
    @Transactional
    public void chargeBalance(UserCommand.SetUserBalance command) {
        User user = userRepository.findByUserId(command.userId())
                .orElseThrow(() -> new NotFoundException(NotFoundException.USER_NOT_FOUND));
        user.setBalance(user.getBalance() + command.amount());
        userRepository.save(user);
    }
}
