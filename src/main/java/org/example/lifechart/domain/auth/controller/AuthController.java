package org.example.lifechart.domain.auth.controller;

import lombok.RequiredArgsConstructor;
import org.example.lifechart.domain.auth.dto.LoginRequest;
import org.example.lifechart.domain.auth.dto.LoginResponse;
import org.example.lifechart.domain.auth.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
