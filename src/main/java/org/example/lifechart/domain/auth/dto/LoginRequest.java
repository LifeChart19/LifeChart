package org.example.lifechart.domain.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequest {

    // 사용자 이메일 (로그인 ID 역할)
    private String email;

    // 사용자 비밀번호
    private String password;
}