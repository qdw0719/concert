package com.hb.concert.support.aspect;

import com.hb.concert.exception.CustomException;
import com.hb.concert.exception.CustomException.QueueTokenException;
import com.hb.concert.support.config.util.JwtUtil;
import com.hb.concert.queueToken.entity.QueueToken;
import com.hb.concert.queueToken.entity.TokenStatus;
import com.hb.concert.queueToken.entity.repository.QueueTokenRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

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

    @Before("@annotation(com.hb.concert.support.annotation.TokenValidation)")
    public void validateToken(JoinPoint joinPoint) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new JwtException("Authorization 헤더가 없거나 잘못되었습니다.");
        }

        String tokenStr = header.substring(7);
        try {
            jwtUtil.validateToken(tokenStr);
        } catch (CustomException.BadRequestException be) {
            throw new CustomException.BadRequestException(QueueTokenException.TOKEN_UNAUTHORIZED);
        }

        QueueToken queueTokenInfo = queueTokenRepository.getTokenInfo(tokenStr).orElseThrow(() -> new QueueTokenException(QueueTokenException.TOKEN_UNAUTHORIZED));

        if (queueTokenInfo.getStatus().equals(TokenStatus.EXPIRED)) {
            throw new CustomException.BadRequestException(QueueTokenException.TOKEN_EXPIREDED);
        }
    }
}