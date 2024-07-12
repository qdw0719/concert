package com.hb.concert.application.user.service;

import com.hb.concert.application.user.command.UserCommand;
import com.hb.concert.common.exception.CustomException;
import com.hb.concert.common.exception.ExceptionMessage;
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
                .orElseThrow(() -> new CustomException.NotFoundException(ExceptionMessage.NOT_FOUND.replace("{msg}", "유저")));
        user.setBalance(user.getBalance() + command.amount());
        userRepository.save(user);
    }
}
