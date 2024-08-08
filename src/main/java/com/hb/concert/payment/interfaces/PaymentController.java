package com.hb.concert.payment.interfaces;

import com.hb.concert.payment.application.facade.PaymentFacade;
import com.hb.concert.payment.interfaces.request.PaymentRequest;
import com.hb.concert.payment.interfaces.response.PaymentResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentFacade paymentFacade;

    public PaymentController(PaymentFacade paymentFacade) {
        this.paymentFacade = paymentFacade;
    }

    @PostMapping("/process")
    public ResponseEntity<PaymentResponse> processedPayment(@RequestBody PaymentRequest request, HttpServletRequest httpRequest) {
        String token = httpRequest.getParameter("token");
        return ResponseEntity.ok(
                PaymentResponse.of(paymentFacade.processedPayment(request.toProcessedCommand(), token))
        );
    }
}
