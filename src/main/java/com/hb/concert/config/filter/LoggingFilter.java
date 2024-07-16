package com.hb.concert.config.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collectors;

@Slf4j
public class LoggingFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 초기화 로직
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Request 로깅
        log.info("Request URL: " + httpRequest.getRequestURL().toString());
        log.info("Request Method: " + httpRequest.getMethod());
        log.info("Request Headers: " + Collections.list(httpRequest.getHeaderNames())
                .stream()
                .collect(Collectors.toMap(h -> h, httpRequest::getHeader)));

        long startTime = System.currentTimeMillis();

        try {
            // 다음 필터 또는 서블릿 실행
            chain.doFilter(request, response);
        } catch (IOException | ServletException e) {
            log.error("Exception in LoggingFilter: " + e.getMessage());
            throw e;
        }

        // Response 로깅
        log.info("Response Status: " + httpResponse.getStatus());
        long duration = System.currentTimeMillis() - startTime;
        log.info("Request Duration: " + duration + " ms");
    }

    @Override
    public void destroy() {
        // 자원 해제 로직
    }
}
