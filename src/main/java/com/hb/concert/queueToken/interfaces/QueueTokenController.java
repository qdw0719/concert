package com.hb.concert.queueToken.interfaces;

import com.hb.concert.queueToken.application.facade.QueueTokenFacade;
import com.hb.concert.queueToken.interfaces.request.QueueTokenRequest;
import com.hb.concert.queueToken.interfaces.response.QueueTokenResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/queue")
public class QueueTokenController {

    public final QueueTokenFacade queueTokenFacade;

    public QueueTokenController(QueueTokenFacade queueTokenFacade) {
        this.queueTokenFacade = queueTokenFacade;
    }

    @PostMapping("/token")
    public ResponseEntity<QueueTokenResponse> getToken(@RequestBody QueueTokenRequest request) {
        return ResponseEntity.ok(
                QueueTokenResponse.tokenInfoOf(queueTokenFacade.issueToken(request.toCreateCommand()))
        );
    }

    @PostMapping("/waiting-info")
    public ResponseEntity<QueueTokenResponse> getWaitingInfo(@RequestBody QueueTokenRequest request) {
        return ResponseEntity.ok(
                QueueTokenResponse.tokenInfoOf(queueTokenFacade.getWaitingInfo(request.toSearchCommand()))
        );
    }
}
