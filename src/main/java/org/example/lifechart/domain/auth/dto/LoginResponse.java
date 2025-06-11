package org.example.lifechart.domain.auth.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class LoginResponse {

    // 클라이언트가 API 호출 시 사용할 accessToken
    private String accessToken;

    // accessToken이 만료됐을 때 재발급 받을 수 있는 refreshToken
    private String refreshToken;
}
