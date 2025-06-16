package org.example.lifechart.auth;

import io.jsonwebtoken.Claims;
import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.auth.dto.LoginRequest;
import org.example.lifechart.domain.auth.dto.TokenRefreshRequest;
import org.example.lifechart.domain.auth.dto.TokenRefreshResponse;
import org.example.lifechart.domain.auth.service.AuthServiceImpl;
import org.example.lifechart.domain.user.entity.User;
import org.example.lifechart.domain.user.repository.UserRepository;
import org.example.lifechart.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private final String email = "test@example.com";
    private final String password = "password";
    private final String encodedPassword = "encoded";
    private final String accessToken = "access-token";
    private final String refreshToken = "refresh-token";
    private final Long userId = 1L;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = User.builder().id(userId).email(email).password(encodedPassword).build();
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    void login_success() {
        LoginRequest request = new LoginRequest(email, password);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
        when(jwtUtil.createAccessToken(userId, email)).thenReturn(accessToken);
        when(jwtUtil.createRefreshToken(userId, email)).thenReturn(refreshToken);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        var result = authService.login(request);

        assertThat(result.getAccessToken()).isEqualTo(accessToken);
        assertThat(result.getRefreshToken()).isEqualTo(refreshToken);
        verify(redisTemplate.opsForValue(), times(1)).set("refresh:" + userId, refreshToken, 7, TimeUnit.DAYS);
    }

    @Test
    @DisplayName("로그인 실패 - 이메일 없음")
    void login_fail_email_not_found() {
        LoginRequest request = new LoginRequest(email, password);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.EMAIL_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void login_fail_password_mismatch() {
        LoginRequest request = new LoginRequest(email, password);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.NOT_MATCH_PASSWORD.getMessage());
    }

    @Test
    @DisplayName("리프레시 토큰 성공 테스트")
    void refreshToken_success() {
        TokenRefreshRequest request = new TokenRefreshRequest(refreshToken);
        when(jwtUtil.validateToken(refreshToken)).thenReturn(true);
        when(jwtUtil.getEmailFromToken(refreshToken)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("refresh:" + userId)).thenReturn(refreshToken);
        when(jwtUtil.createAccessToken(userId, email)).thenReturn(accessToken);
        when(jwtUtil.createRefreshToken(userId, email)).thenReturn("new-refresh");

        TokenRefreshResponse response = authService.refresh(request);

        assertThat(response.getAccessToken()).isEqualTo(accessToken);
        assertThat(response.getRefreshToken()).isEqualTo("new-refresh");    }

    @Test
    @DisplayName("리프레시 토큰 실패 - 유효하지 않은 토큰")
    void refreshToken_fail_invalid_token() {
        TokenRefreshRequest request = new TokenRefreshRequest(refreshToken);
        when(jwtUtil.validateToken(refreshToken)).thenReturn(false);

        assertThatThrownBy(() -> authService.refresh(request))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.INVALID_REFRESH_TOKEN.getMessage());
    }

    @Test
    @DisplayName("로그아웃 성공 - 블랙리스트 등록")
    void logout_success_with_blacklist() {
        when(jwtUtil.getClaims(accessToken)).thenReturn(mock(Claims.class));
        when(jwtUtil.getClaims(accessToken).getExpiration()).thenReturn(new java.util.Date(System.currentTimeMillis() + 60000));
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        authService.logout(userId, accessToken);

        verify(redisTemplate, times(1)).delete("refresh:" + userId);
        verify(redisTemplate.opsForValue(), times(1)).set(startsWith("blacklist:"), eq("logout"), anyLong(), eq(TimeUnit.MILLISECONDS));
    }
}
