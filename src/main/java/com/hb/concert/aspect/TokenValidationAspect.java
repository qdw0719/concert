package com.hb.concert.aspect;

import com.hb.concert.common.exception.BadRequestException;
import com.hb.concert.config.util.JwtUtil;
import com.hb.concert.domain.queue.QueueToken;
import com.hb.concert.domain.queue.QueueTokenRepository;
import com.hb.concert.presentation.concert.ConcertRequest;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Aspect @Component
public class TokenValidationAspect {

    private final HttpServletRequest request;
    private final QueueTokenRepository queueTokenRepository;
    private final JwtUtil jwtUtil;

    public TokenValidationAspect(HttpServletRequest request, QueueTokenRepository queueTokenRepository, JwtUtil jwtUtil) {
        this.request = request;
        this.queueTokenRepository = queueTokenRepository;
        this.jwtUtil = jwtUtil;
    }

    @Before("@annotation(com.hb.concert.annotation.TokenValidation)")
    public void validateToken(JoinPoint joinPoint) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new JwtException("Authorization 헤더가 없거나 잘못되었습니다.");
        }

        String tokenStr = header.substring(7);
        UUID userId;
        try {
            userId = jwtUtil.getUserIdFromToken(tokenStr);
            jwtUtil.validateToken(tokenStr);
        } catch (JwtException e) {
            throw new JwtException("유효하지 않은 토큰입니다.");
        }

        QueueToken queueToken = queueTokenRepository.findByToken(tokenStr);
        if (queueToken.getIsActive().equals("N")) {
            throw new BadRequestException("유효하지 않은 토큰입니다.");
        }
        if (queueToken.getStatus().equals(QueueToken.TokenStatus.EXPIRED)) {
            throw new BadRequestException("만료된 토큰입니다. 처음부터 다시 진행해 주세요.");
        }

        // ConcertRequest에 userId 설정
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof ConcertRequest) {
                ConcertRequest originalRequest = (ConcertRequest) args[i];
                originalRequest.setUserId(userId);
            }
        }
    }
}
