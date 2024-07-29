package com.hb.concert.support.config.interceptor;

import com.hb.concert.domain.exception.CustomException.QueueTokenException;
import com.hb.concert.domain.queueToken.service.QueueTokenService;
import com.hb.concert.support.config.util.JwtUtil;
import com.hb.concert.domain.queueToken.QueueToken;
import com.hb.concert.domain.queueToken.QueueToken.TokenStatus;
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

    @Autowired private JwtUtil jwtUtil;
    @Autowired private QueueTokenService queueTokenService;

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
            try {
                UUID userId = jwtUtil.getUserIdFromToken(tokenStr);
                log.info("Request user ID : {}", userId);
                jwtUtil.validateToken(tokenStr);
            } catch (JwtException je) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, QueueTokenException.TOKEN_UNAUTHORIZED);
                log.error("Token validation check, JwtException >> {}", je.getMessage());
                return false;
            }

            QueueToken queueToken = queueTokenService.getTokenInfo(tokenStr);
            if (queueToken.getStatus().equals(QueueToken.TokenStatus.EXPIRED)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, QueueTokenException.TOKEN_UNAUTHORIZED);
                log.info("Token status check, status : {}", queueToken.getStatus());
                return false;
            }

            int tokenPosition = jwtUtil.getPositionFromToken(tokenStr);
            if (tokenPosition != 0) {
                throw new QueueTokenException(String.format("{}, 현재대기 순번: {}", QueueTokenException.TOKEN_NOT_POSITION_ZERO, tokenPosition));
            } else if (tokenPosition == 0 && queueToken.getStatus().equals(TokenStatus.WAIT)){
                queueTokenService.updateWaitToProcessStatus(tokenStr);
            }

            if ("/api/payment/process".equals(request.getRequestURI())) {
                CustomHttpServletRequestWrapper requestWrapper = new CustomHttpServletRequestWrapper(request);
                requestWrapper.addParameter("token", tokenStr);
                request = requestWrapper;
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }
}