package org.example.lifechart.domain.auth.controller;

import lombok.RequiredArgsConstructor;
import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.enums.SuccessCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.common.response.ApiResponse;
import org.example.lifechart.domain.auth.dto.LoginRequest;
import org.example.lifechart.domain.auth.dto.LoginResponse;
import org.example.lifechart.domain.auth.service.AuthService;
import org.example.lifechart.security.CustomUserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;


@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ApiResponse.onSuccess(SuccessCode.SUCCESS_USER_LOGIN, response);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Object>> logout(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @RequestHeader("Authorization") String authorizationHeader) {

        String accessToken = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            accessToken = authorizationHeader.substring(7);
        } else {
            throw new RuntimeException("AccessToken이 필요합니다.");
        }

        authService.logout(userPrincipal.getUserId(), accessToken);
        return ApiResponse.onSuccess(SuccessCode.SUCCESS_USER_LOGOUT, null);
    }
}
