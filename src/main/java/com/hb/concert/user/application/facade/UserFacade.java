package com.hb.concert.user.application.facade;

import com.hb.concert.user.application.UserCommand;
import com.hb.concert.user.entity.User;
import com.hb.concert.user.entity.service.UserService;
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
