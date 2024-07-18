package com.hb.concert.presentation.user;

import com.hb.concert.application.user.command.UserCommand;
import com.hb.concert.application.user.facade.UserFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/users")
public class UserController {

    private final UserFacade userFacade;

    public UserController(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    /**
     * 유저의 잔액을 충전하는 API
     *
     * @param request 잔액 충전을 위한 요청
     * @return ResponseEntity<Void>
     */
    @PostMapping("/charge")
    public ResponseEntity<Void> chargeBalance(@RequestBody ChargeRequest request) {
        UserCommand.SetUserBalance command = new UserCommand.SetUserBalance(request.userId(), request.amount());
        userFacade.chargeBalance(command);
        return ResponseEntity.ok().build();
    }
}
