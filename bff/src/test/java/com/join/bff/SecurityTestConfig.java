package com.join.bff;

import com.join.bff.infrastructure.filter.JwtTokenFilter;
import com.join.bff.infrastructure.util.JwtTokenProvider;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class SecurityTestConfig {

    @Bean
    @Primary
    public JwtTokenProvider jwtTokenProvider() {
        return mock(JwtTokenProvider.class);
    }

    @Bean
    @Primary
    public JwtTokenFilter jwtTokenFilter() {
        JwtTokenFilter filter = mock(JwtTokenFilter.class);
        return filter;
    }
}
