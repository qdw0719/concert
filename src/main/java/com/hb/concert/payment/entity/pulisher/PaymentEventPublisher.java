package com.hb.concert.payment.entity.pulisher;

import com.hb.concert.payment.dataplatform.PaymentCompleteEvent;
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
