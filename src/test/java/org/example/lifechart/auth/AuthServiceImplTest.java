package org.example.lifechart.auth;

import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.auth.dto.LoginRequest;
import org.example.lifechart.domain.auth.dto.LoginResponse;
import org.example.lifechart.domain.auth.service.AuthServiceImpl;
import org.example.lifechart.domain.user.entity.User;
import org.example.lifechart.domain.user.repository.UserRepository;
import org.example.lifechart.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;
    @Mock private StringRedisTemplate redisTemplate;
    @Mock private ValueOperations<String, String> valueOperations;

    @InjectMocks private AuthServiceImpl authService;

    //정상 로그인 시 AccessToken과 RefreshToken이 올바르게 반환되는지 검증
    @Test
    void login_success_returnsTokens() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        String email = "test@example.com";
        String rawPassword = "password";
        String encodedPassword = "encodedPassword";
        Long userId = 1L;

        LoginRequest request = new LoginRequest(email, rawPassword);

        User user = User.builder()
                .id(userId)
                .email(email)
                .password(encodedPassword)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);
        when(jwtUtil.createAccessToken(userId, email)).thenReturn("access-token");
        when(jwtUtil.createRefreshToken(userId, email)).thenReturn("refresh-token");

        LoginResponse response = authService.login(request);

        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
    }

    //정상 로그인 시 JWT 토큰 생성과 Redis 연산 메서드가 호출되는지 검증
    @Test
    void login_success_callsJwtAndRedisMethods() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        String email = "test@example.com";
        String rawPassword = "password";
        String encodedPassword = "encodedPassword";
        Long userId = 1L;

        LoginRequest request = new LoginRequest(email, rawPassword);

        User user = User.builder()
                .id(userId)
                .email(email)
                .password(encodedPassword)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);
        when(jwtUtil.createAccessToken(userId, email)).thenReturn("access-token");
        when(jwtUtil.createRefreshToken(userId, email)).thenReturn("refresh-token");

        authService.login(request);

        verify(jwtUtil).createAccessToken(userId, email);
        verify(jwtUtil).createRefreshToken(userId, email);
        verify(redisTemplate).opsForValue();
        // 추가 Redis 연산 메서드 verify 필요시 여기에 추가
    }

    //존재하지 않는 이메일로 로그인 시 CustomException 예외 발생 검증
    @Test
    void login_emailNotFound_throwsException() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        LoginRequest request = new LoginRequest("wrong@example.com", "password");

        CustomException exception = assertThrows(CustomException.class, () -> authService.login(request));
        assertEquals(ErrorCode.EMAIL_NOT_FOUND, exception.getErrorCode());
    }

    //비밀번호 불일치 시 CustomException 예외 발생 검증
    @Test
    void login_passwordMismatch_throwsException() {
        String email = "test@example.com";
        String rawPassword = "wrongPassword";
        String encodedPassword = "encodedPassword";

        User user = User.builder()
                .email(email)
                .password(encodedPassword)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);

        LoginRequest request = new LoginRequest(email, rawPassword);

        CustomException exception = assertThrows(CustomException.class, () -> authService.login(request));
        assertEquals(ErrorCode.NOT_MATCH_PASSWORD, exception.getErrorCode());
    }
}
