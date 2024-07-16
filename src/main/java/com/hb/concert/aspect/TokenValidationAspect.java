package com.hb.concert.aspect;

import com.hb.concert.domain.exception.CustomException;
import com.hb.concert.domain.exception.CustomException.BadRequestException;
import com.hb.concert.config.util.JwtUtil;
import com.hb.concert.domain.queue.QueueToken;
import com.hb.concert.domain.queue.QueueTokenRepository;
import com.hb.concert.presentation.concert.ConcertRequest;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Before;

import java.util.UUID;

//@Aspect @Component
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
        } catch (CustomException.BadRequestException be) {
            throw new CustomException.BadRequestException(BadRequestException.TOKEN_UNAUTHORIZED);
        }

        QueueToken queueToken = queueTokenRepository.findByToken(tokenStr);
        if (queueToken.getIsActive().equals("N")) {
            throw new CustomException.BadRequestException(BadRequestException.TOKEN_UNAUTHORIZED);
        }
        if (queueToken.getStatus().equals(QueueToken.TokenStatus.EXPIRED)) {
            throw new CustomException.BadRequestException(BadRequestException.TOKEN_UNAUTHORIZED);
        }

        // ConcertRequest에 userId 설정
//        Object[] args = joinPoint.getArgs();
//        for (int i = 0; i < args.length; i++) {
//            if (args[i] instanceof ConcertRequest) {
//                ConcertRequest originalRequest = (ConcertRequest) args[i];
//                originalRequest.setUserId(userId);
//            }
//        }
    }
}
