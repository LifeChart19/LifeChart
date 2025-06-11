package org.example.lifechart.domain.auth.service;

import org.example.lifechart.domain.auth.dto.LoginRequest;
import org.example.lifechart.domain.auth.dto.LoginResponse;
import org.example.lifechart.domain.auth.dto.TokenRefreshRequest;
import org.example.lifechart.domain.auth.dto.TokenRefreshResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);
    void logout(Long userId, String accessToken);
    TokenRefreshResponse refresh(TokenRefreshRequest request);

}
