package com.hb.concert.support.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("콘서트 예약 서비스 API")
                        .version("1.0")
                        .description("This is a sample Spring Boot RESTful service using Swagger"));
    }
}