package com.hb.concert.domain.dataplatform;

import com.hb.concert.domain.common.enumerate.UseYn;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @Builder
@AllArgsConstructor @NoArgsConstructor
@Entity @Table(name = "HB_DATAPLATFORM_FAILED")
public class DataPlatformFailed {
    @Id
    private Long id;
    @Enumerated(EnumType.STRING)
    private UseYn resendYn;
    private String failMessage;
    private String sendKey;
    private LocalDateTime createdAt;
    private LocalDateTime resendTime;
}
