package com.hb.concert.presentation.queue;

import com.hb.concert.application.queue.facade.QueueFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/queue")
public class QueueController {

    private final QueueFacade queueFacade;

    @Autowired
    public QueueController(QueueFacade queueFacade) {
        this.queueFacade = queueFacade;
    }

    /**
     * 대기열 토큰을 생성
     * @param request 토큰 생성 요청 정보
     * @return 생성된 토큰 정보
     */
    @PostMapping("/token")
    public ResponseEntity<QueueTokenResponse> generateToken(@RequestBody QueueTokenRequest request) {
        return ResponseEntity.ok(
                QueueTokenResponse.of(queueFacade.generateToken(request.toGenerateCommand()))
        );
    }

    /**
     * 대기열에서 다음 토큰을 처리
     * @return 처리된 토큰 정보
     */
    @PostMapping("/process")
    public void processCompletedToken(QueueTokenRequest request) {
        queueFacade.processCompletedToken(request.toTokenCompleted());
    }
}