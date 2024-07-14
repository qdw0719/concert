package com.hb.concert.config.interceptor;

import com.hb.concert.common.exception.ExceptionMessage;
import com.hb.concert.config.util.JwtUtil;
import com.hb.concert.domain.common.enumerate.UseYn;
import com.hb.concert.domain.queue.QueueToken;
import com.hb.concert.domain.queue.QueueTokenRepository;
import com.hb.concert.presentation.concert.ConcertRequest;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Component @Slf4j
public class TokenValidationInterceptor implements HandlerInterceptor {

    @Autowired private QueueTokenRepository queueTokenRepository;
    @Autowired private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;

            String header = request.getHeader("Authorization");
            if (header == null || !header.startsWith("Bearer ")) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "토큰이 없는 사용자입니다.");
                return false;
            }

            String tokenStr = header.substring(7);

            UUID userId;
            try {
                userId = jwtUtil.getUserIdFromToken(tokenStr);
                jwtUtil.validateToken(tokenStr);
            } catch (JwtException je) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ExceptionMessage.UNAUTHORIZED);
                log.info("Token validation check, JwtException >> {}", je.getMessage());
                return false;
            }

            QueueToken queueToken = queueTokenRepository.findByToken(tokenStr);
            if (queueToken.getIsActive().equals(UseYn.N)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ExceptionMessage.UNAUTHORIZED);
                log.info("Token IsActive check, isActive : {}", queueToken.getIsActive());
                return false;
            }
            if (queueToken.getStatus().equals(QueueToken.TokenStatus.EXPIRED)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ExceptionMessage.TOKEN_EXPIRED);
                log.info("Token status check, status : {}", queueToken.getStatus());
                return false;
            }
            // ConcertRequest에 userId 설정
            Object[] args = handlerMethod.getMethodParameters();
            for (Object arg : args) {
                if (arg instanceof ConcertRequest) {
                    ConcertRequest originRequest = (ConcertRequest) arg;
                    originRequest.setUserId(userId);
                }
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }
}