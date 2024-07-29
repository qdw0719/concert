package com.hb.concert.interfaces.queueToken;

import com.hb.concert.application.queueToken.facade.QueueTokenFacade;
import com.hb.concert.interfaces.queueToken.request.QueueTokenRequest;
import com.hb.concert.interfaces.queueToken.response.QueueTokenResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping("/api/queue")
public class QueueTokenController {

    public final QueueTokenFacade queueTokenFacade;

    public QueueTokenController(QueueTokenFacade queueTokenFacade) {
        this.queueTokenFacade = queueTokenFacade;
    }

    @GetMapping("/token")
    public ResponseEntity<QueueTokenResponse> getToken(@RequestBody QueueTokenRequest request) {
        return ResponseEntity.ok(
                QueueTokenResponse.tokenInfoOf(queueTokenFacade.generateToken(request.toCreateCommand()))
        );
    }

    @GetMapping("/waiting-info")
    public ResponseEntity<QueueTokenResponse> getWaitingInfo(@RequestBody QueueTokenRequest request) {
        return ResponseEntity.ok(
                QueueTokenResponse.tokenInfoOf(queueTokenFacade.getWaitingInfo(request.toSearchCommand()))
        );
    }
}
