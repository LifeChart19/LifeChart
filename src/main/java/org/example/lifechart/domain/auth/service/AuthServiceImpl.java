package org.example.lifechart.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.example.lifechart.domain.auth.dto.LoginRequest;
import org.example.lifechart.domain.auth.dto.LoginResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Override
    public LoginResponse login(LoginRequest request) {
        //로그인 로직 구현 전
        return null;
    }
}
