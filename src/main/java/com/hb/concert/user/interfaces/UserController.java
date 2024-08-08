package com.hb.concert.user.interfaces;

import com.hb.concert.user.application.facade.UserFacade;
import com.hb.concert.user.interfaces.request.UserRequest;
import com.hb.concert.user.interfaces.response.UserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping("/api/user")
public class UserController {

    private final UserFacade userFacade;

    public UserController(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    @PostMapping("/charge")
    public ResponseEntity<UserResponse> charge(UserRequest request) {
        return ResponseEntity.ok(
                UserResponse.of(userFacade.charge(request.toBalanceCommand()))
        );
    }

    @PostMapping("/consume")
    public ResponseEntity<UserResponse> consume(UserRequest request) {
        return ResponseEntity.ok(
                UserResponse.of(userFacade.consume(request.toBalanceCommand()))
        );
    }
}
