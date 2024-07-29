package com.hb.concert.application.user.facade;

import com.hb.concert.application.user.UserCommand;
import com.hb.concert.domain.user.User;
import com.hb.concert.domain.user.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserFacade {

    private final UserService userService;

    public UserFacade(UserService userService) {
        this.userService = userService;
    }

    public User charge(UserCommand.Balance command) {
        return userService.charge(command.userId(), command.amount());
    }

    public User consume(UserCommand.Balance command) {
        return userService.consume(command.userId(), command.amount());
    }
}
