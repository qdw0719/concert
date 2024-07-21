package com.hb.concert.domain.queue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor @AllArgsConstructor
@Data @Builder
public class QueueConfiguration {

    private Long id;

    // 최대 활성화 시킬 유저 수
    private Integer maxActiveUser;

    // 최대 대기 시간
    private Integer maxQueueTime;

    // 한 번에 활성화 할 유저 수
    private Integer maxUserPerInterval;

    // 활성화 간격
    private Integer intervalMinute;
}
