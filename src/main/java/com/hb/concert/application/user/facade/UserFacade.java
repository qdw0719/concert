package com.hb.concert.application.user.facade;

import com.hb.concert.application.user.command.UserCommand;
import com.hb.concert.domain.user.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserFacade {

    private final UserService userService;

    public UserFacade(UserService userService) {
        this.userService = userService;
    }

    public void chargeBalance(UserCommand.SetUserBalance command) {
        userService.chargeBalance(command);
    }
}
