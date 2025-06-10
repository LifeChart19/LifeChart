package org.example.lifechart.domain.auth.service;

import org.example.lifechart.domain.auth.dto.LoginRequest;
import org.example.lifechart.domain.auth.dto.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);
    void logout(Long userId);
}
