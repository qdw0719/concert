package com.hb.concert.domain.payment.pulisher;

import com.hb.concert.domain.dataplatform.PaymentCompleteEvent;
//import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventPublisher {
//    private final ApplicationEventPublisher applicationEventPublisher;

//    public PaymentEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
//        this.applicationEventPublisher = applicationEventPublisher;
//    }
//
//    public void complete(PaymentCompleteEvent event) {
//        applicationEventPublisher.publishEvent(event);
//    }

    // ApplicationEventPublisher -> Kafka 로 변경
    private final KafkaTemplate<String, PaymentCompleteEvent> kafkaTemplate;

    public PaymentEventPublisher(KafkaTemplate<String, PaymentCompleteEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void complete(PaymentCompleteEvent event) {
        kafkaTemplate.send("payment-complete", event);
    }
}
