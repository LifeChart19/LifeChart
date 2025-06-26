package org.example.lifechart.common.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.codec.ErrorDecoder;
import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;
import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                String token = extractAccessTokenFromRequest();
                if (token != null) {
                    template.header("Authorization", "Bearer " + token);
                }
            }
        };
    }

    // HttpServletRequest에서 토큰 추출 (최적!)
    private String extractAccessTokenFromRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) return null;
        HttpServletRequest request = attributes.getRequest();
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            HttpStatus status = HttpStatus.valueOf(response.status());
            if (status == HttpStatus.UNAUTHORIZED) {
                return new CustomException(ErrorCode.INVALID_JWT_SIGNATURE);
            }
            if (status == HttpStatus.NOT_FOUND) {
                return new CustomException(ErrorCode.ACCOUNT_NOT_FOUND);
            }
            return new RuntimeException(
                    "FeignClient Error: " + status + " - " + response.reason()
            );
        };
    }
}
