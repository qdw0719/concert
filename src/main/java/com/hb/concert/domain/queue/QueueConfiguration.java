package com.hb.concert.domain.queue;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@NoArgsConstructor @AllArgsConstructor
@Data @Builder
@Entity @Table(name = "HB_QUEUE_CONFIGURATION")
public class QueueConfiguration {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("최대 활성화 시킬 유저 수")
    private Integer maxActiveUser;

    @Comment("최대 대기 시간")
    private Integer maxQueueTime;

    @Comment("한 번에 활성화 할 유저 수")
    private Integer maxUserPerInterval;

    @Comment("활성화 간격")
    private Integer intervalMinute;
}
