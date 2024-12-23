package com.hb.concert.payment.entity.listener;

import com.hb.concert.payment.dataplatform.DataPlatformService;
import com.hb.concert.payment.dataplatform.PaymentCompleteEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class PaymentEventListener {

    private final DataPlatformService dataPlatformService;

    public PaymentEventListener(DataPlatformService dataPlatformService) {
        this.dataPlatformService = dataPlatformService;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void paymentSuccessHandler(PaymentCompleteEvent event) {
        dataPlatformService.send(event);
    }

}
