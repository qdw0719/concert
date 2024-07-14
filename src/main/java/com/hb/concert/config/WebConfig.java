package com.hb.concert.config;

import com.hb.concert.config.interceptor.TokenValidationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private TokenValidationInterceptor tokenValidationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenValidationInterceptor)
                .addPathPatterns(
                        "/api/reservations",
                        "/api/payments/create",
                        "/api/concerts/**/details",
                        "/api/concerts/**/details/**/seat"
                );
    }
}
