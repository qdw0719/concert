package com.hb.concert.payment.dataplatform;

import org.springframework.stereotype.Service;

@Service
public class DataPlatformService {

    public String send(PaymentCompleteEvent event) {
        return "Success";
    }
}
