package com.hb.concert.domain.payment.dataplatform;

import org.springframework.stereotype.Service;

@Service
public class DataPlatformService {

    public String send(PaymentCompleteEvent event) {
        return "Success";
    }
}
