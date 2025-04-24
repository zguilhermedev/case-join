package com.join.bff.infrastructure.config;

import com.join.bff.infrastructure.interceptor.JwtAuthorizationInterceptor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .interceptors(List.of(new JwtAuthorizationInterceptor()))
                .build();
    }
}
