package com.hb.concert.domain.payment.listener;

//import com.hb.concert.domain.payment.dataplatform.DataPlatformService;
//import com.hb.concert.domain.payment.dataplatform.PaymentCompleteEvent;
//import org.springframework.scheduling.annotation.Async;
import com.hb.concert.domain.common.enumerate.UseYn;
import com.hb.concert.domain.dataplatform.DataPlatformFailed;
import com.hb.concert.domain.dataplatform.repository.DataPlatformFailedRepository;
import com.hb.concert.domain.dataplatform.service.DataPlatformService;
import com.hb.concert.domain.dataplatform.PaymentCompleteEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
//import org.springframework.transaction.event.TransactionPhase;
//import org.springframework.transaction.event.TransactionalEventListener;

@Component @Slf4j
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

    private final DataPlatformFailedRepository dataPlatformFailedRepository;

    public PaymentEventListener(DataPlatformService dataPlatformService, DataPlatformFailedRepository dataPlatformFailedRepository) {
        this.dataPlatformService = dataPlatformService;
        this.dataPlatformFailedRepository = dataPlatformFailedRepository;
    }

    @KafkaListener(topics = "payment-complete", groupId = "send-dataplatform")
    public void paymentSuccessHandler(PaymentCompleteEvent event) {
        try {
            dataPlatformService.send(event);
        } catch (RuntimeException e) {
            String message = "dataplatform 전송에 실패했습니다.";
            DataPlatformFailed failedData = DataPlatformFailed.builder()
                    .resendYn(UseYn.N)
                    .sendKey(event.reservationId())
                    .failMessage(message)
                    .createdAt(LocalDateTime.now())
                    .build();
            dataPlatformFailedRepository.save(failedData);
            log.warn("{} >> {}", message, event.reservationId());
        }
    }
}
