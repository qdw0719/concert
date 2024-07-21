package com.hb.concert.support.config.interceptor;

import com.hb.concert.domain.exception.CustomException;
import com.hb.concert.support.config.util.JwtUtil;
import com.hb.concert.domain.common.enumerate.UseYn;
import com.hb.concert.domain.queue.QueueToken;
import com.hb.concert.domain.queue.QueueTokenRepository;
import com.hb.concert.presentation.concert.ConcertRequest;
import com.hb.concert.presentation.concert.ConcertSeatRequest;
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
                log.info("Request user ID : {}", userId);
                jwtUtil.validateToken(tokenStr);
            } catch (JwtException je) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, CustomException.BadRequestException.TOKEN_UNAUTHORIZED);
                log.error("Token validation check, JwtException >> {}", je.getMessage());
                return false;
            }

            QueueToken queueToken = queueTokenRepository.findByToken(tokenStr);
            if (queueToken.getStatus().equals(QueueToken.TokenStatus.EXPIRED)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, CustomException.BadRequestException.TOKEN_UNAUTHORIZED);
                log.error("Token status check, status : {}", queueToken.getStatus());
                return false;
            }
            if (queueToken.getIsActive().equals(UseYn.N)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, CustomException.BadRequestException.TOKEN_UNAUTHORIZED);
                log.error("Token IsActive check, isActive : {}", queueToken.getIsActive());
                return false;
            }

            // ConcertRequest에 userId, token 설정
            String requestUri = request.getRequestURI();
            if (requestUri.matches("/api/concerts/.*/details")) {
                ConcertRequest concertRequest = new ConcertRequest(userId, "", tokenStr);
                request.setAttribute("concertRequest", concertRequest);
            } else if (requestUri.matches("/api/concerts/.*/details/.*/seats")) {
                ConcertSeatRequest concertSeatRequest = new ConcertSeatRequest(userId, "", "", tokenStr);
                request.setAttribute("concertSeatRequest", concertSeatRequest);
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }
}