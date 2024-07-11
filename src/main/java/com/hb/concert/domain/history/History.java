package com.hb.concert.domain.history;

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
@Entity
@Table(
        name = "HB_HISTORY",
        indexes = {
                @Index(name = "idx_hb_history_userId", columnList = "userId"),
                @Index(name = "idx_hb_history_regTime", columnList = "regDate")
        }
)
public class History {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("유저 UUID") @Column(nullable = false)
    private UUID userId;

    @Comment("히스토리 타입(충전, 예약, 결제)") @Enumerated(EnumType.STRING)
    private HistoryType type;

    @Comment("등록시간") @Column(nullable = false)
    private LocalDateTime regDate;

    @Comment("타입에 대한 상태(성공/실패)") @Enumerated(EnumType.STRING)
    private HistoryStatus status;

    @Comment("실패 이유")
    private String failReason;

    public enum HistoryStatus {
        SUCCESS, FAIL
    }

    public enum HistoryType {
        RESERVATION, PAYMENT
    }
}
