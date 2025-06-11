package org.example.lifechart.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenRefreshResponse {
    private String accessToken;
    private String refreshToken; // (필요시. 재발급이면 새로 반환, 아니면 기존 것 그대로)
}

