package com.hb.concert.domain.payment.listener;

//import com.hb.concert.domain.payment.dataplatform.DataPlatformService;
//import com.hb.concert.domain.payment.dataplatform.PaymentCompleteEvent;
//import org.springframework.scheduling.annotation.Async;
import com.hb.concert.domain.payment.dataplatform.DataPlatformService;
import com.hb.concert.domain.payment.dataplatform.PaymentCompleteEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
//import org.springframework.transaction.event.TransactionPhase;
//import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class PaymentEventListener {

//    private final DataPlatformService dataPlatformService;
//
//    public PaymentEventListener(DataPlatformService dataPlatformService) {
//        this.dataPlatformService = dataPlatformService;
//    }
//
//    @Async
//    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
//    public void paymentSuccessHandler(PaymentCompleteEvent event) {
//        dataPlatformService.send(event);
//    }

    // ApplicationEventPublisher -> Kafka 로 변경
    private final DataPlatformService dataPlatformService;

    public PaymentEventListener(DataPlatformService dataPlatformService) {
        this.dataPlatformService = dataPlatformService;
    }

    @KafkaListener(topics = "payment-complete", groupId = "group1")
    public void paymentSuccessHandler(PaymentCompleteEvent event) {
        dataPlatformService.send(event);
    }

}
