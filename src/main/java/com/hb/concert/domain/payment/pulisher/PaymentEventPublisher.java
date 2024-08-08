package com.hb.concert.domain.payment.pulisher;

import com.hb.concert.domain.payment.dataplatform.PaymentCompleteEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    public PaymentEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void complete(PaymentCompleteEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}
