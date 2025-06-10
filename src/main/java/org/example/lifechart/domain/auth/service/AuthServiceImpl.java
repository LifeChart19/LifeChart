package org.example.lifechart.domain.auth.service;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.auth.dto.LoginRequest;
import org.example.lifechart.domain.auth.dto.LoginResponse;
import org.example.lifechart.domain.user.entity.User;
import org.example.lifechart.domain.user.repository.UserRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.example.lifechart.security.JwtUtil;

import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redisTemplate;


    private final long refreshTokenValidityMs = 1000L * 60 * 60 * 24 * 7; // 7일

    @Override
    public LoginResponse login(LoginRequest request) {

        // 이메일로 유저 조회, 실패 시 예외 처리
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.EMAIL_NOT_FOUND));

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.NOT_MATCH_PASSWORD);
        }

        // JWT 토큰 생성
        String accessToken = jwtUtil.createAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtUtil.createRefreshToken(user.getId(), user.getEmail());

        // refreshToken 저장
        redisTemplate.opsForValue().set(
                "refresh:" + user.getId(),
                refreshToken,
                7, TimeUnit.DAYS // 유효기간 설정 (선택)
        );

        return new LoginResponse(accessToken, refreshToken);
    }

    @Override
    public void logout(Long userId, String accessToken) {
        // RefreshToken 삭제
        redisTemplate.delete("refresh:" + userId);

        // AccessToken 블랙리스트 등록 (남은 만료시간 계산)
        Claims claims = jwtUtil.getClaims(accessToken);
        long expirationTime = claims.getExpiration().getTime() - System.currentTimeMillis();
        if (expirationTime > 0) {
            redisTemplate.opsForValue().set("blacklist:" + accessToken, "logout", expirationTime, TimeUnit.MILLISECONDS);
        }
    }
}
