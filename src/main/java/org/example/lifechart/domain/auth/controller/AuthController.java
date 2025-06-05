package org.example.lifechart.domain.auth.controller;

import lombok.RequiredArgsConstructor;
import org.example.lifechart.common.dto.CommonResponseDto;
import org.example.lifechart.common.enums.SuccessCode;
import org.example.lifechart.domain.auth.dto.LoginRequest;
import org.example.lifechart.domain.auth.dto.LoginResponse;
import org.example.lifechart.domain.auth.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<CommonResponseDto<LoginResponse>> login(@RequestBody @Validated LoginRequest request) {
        LoginResponse response = authService.login(request);

        return ResponseEntity
                .status(SuccessCode.SUCCESS_USER_LOGIN.getStatus())
                .body(CommonResponseDto.of(SuccessCode.SUCCESS_USER_LOGIN, response));
    }
}
