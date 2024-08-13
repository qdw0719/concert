package com.hb.concert.interfaces.payment;

import com.hb.concert.application.payment.facade.PaymentFacade;
import com.hb.concert.interfaces.payment.request.PaymentRequest;
import com.hb.concert.interfaces.payment.response.PaymentResponse;
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