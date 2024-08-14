package com.hb.concert.domain.dataplatform.service;

import com.hb.concert.domain.common.enumerate.UseYn;
import com.hb.concert.domain.dataplatform.DataPlatformFailed;
import com.hb.concert.domain.dataplatform.PaymentCompleteEvent;
import com.hb.concert.domain.dataplatform.repository.DataPlatformFailedRepository;
import com.hb.concert.support.CommonUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DataPlatformService {

    private final DataPlatformFailedRepository dataPlatformFailedRepository;

    public DataPlatformService(DataPlatformFailedRepository dataPlatformFailedRepository) {
        this.dataPlatformFailedRepository = dataPlatformFailedRepository;
    }

    public String send(PaymentCompleteEvent event) {
        if (event.reservationId().equals("reservation1")) { // 번 예약은 강제로 실패처리
            throw new RuntimeException("Send failed");
        }
        return "Success";
    }

    @Transactional
    public void resend() {
        List<DataPlatformFailed> resendTargetList = dataPlatformFailedRepository.getResendTargetList();
        resendTargetList.forEach(target -> {
            target.setResendYn(UseYn.Y);
            target.setResendTime(LocalDateTime.now());
            dataPlatformFailedRepository.save(target);

            PaymentCompleteEvent paymentCompleteEvent = new PaymentCompleteEvent(target.getSendKey());
            send(paymentCompleteEvent);
        });
    }
}
