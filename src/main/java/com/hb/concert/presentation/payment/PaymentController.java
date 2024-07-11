package com.hb.concert.presentation.payment;

import com.hb.concert.annotation.TokenValidation;
import com.hb.concert.application.payment.facade.PaymentFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentFacade paymentFacade;

    public PaymentController(PaymentFacade paymentFacade) {
        this.paymentFacade = paymentFacade;
    }

    /**
     * 결제 생성 API
     *
     * @param requestBody PaymentRequest
     * @return ResponseEntity<PaymentResponse>
     */
    @PostMapping("/create") @TokenValidation
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody PaymentRequest requestBody) {
        return ResponseEntity.ok(PaymentResponse.of(paymentFacade.createPayment(requestBody.toCommand())));
    }
}
