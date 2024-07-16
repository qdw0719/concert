package com.hb.concert.domain.user.service;

import com.hb.concert.application.user.command.UserCommand;
import com.hb.concert.domain.exception.CustomException;
import com.hb.concert.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


class UserServiceTest {

    @InjectMocks private UserService userService;

    @Mock private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 초기 데이터 DataInitialize.class 로 대체
    }

    @Test
    void 유저_잔액_충전_시_잘못된_userId라면_UserNotFoundException() {
        UUID userId = UUID.randomUUID();
        int amount = 10000000;

        UserCommand.SetUserBalance command = new UserCommand.SetUserBalance(userId, amount);

        assertThrows(CustomException.NotFoundException.class, () -> {
            userService.chargeBalance(command);
        });
    }


}