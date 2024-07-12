package com.hb.concert.domain.queue;

import com.hb.concert.domain.common.enumerate.UseYn;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor @AllArgsConstructor
@Data @Builder
@Entity @Table(name = "HB_QUEUE_TOKEN")
public class QueueToken {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("대기열 토큰") @Column(nullable = false, length = 2000)
    private String token;

    @Comment("유저 UUID") @Column(nullable = false)
    private UUID userId;

    @Comment("대기 순번, 위치")
    private Integer position;

    @Comment("대기 시간")
    private Integer waitTime;

    @Comment("토큰 발급 시간")
    private LocalDateTime createdTime;

    @Comment("활성화 여부")
    private UseYn isActive;

    @Comment("토큰 상태")
    private TokenStatus status;

    public enum TokenStatus {
        WAIT, PROCESS, EXPIRED
    }
}
