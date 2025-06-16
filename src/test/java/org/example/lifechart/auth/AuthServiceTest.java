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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
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
        user = User.builder().id(userId).email(email).password(encodedPassword).build();
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    void login_success() {
        LoginRequest request = new LoginRequest(email, password);

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(password, encodedPassword)).willReturn(true);
        given(jwtUtil.createAccessToken(userId, email)).willReturn(accessToken);
        given(jwtUtil.createRefreshToken(userId, email)).willReturn(refreshToken);
        given(redisTemplate.opsForValue()).willReturn(valueOperations);

        var result = authService.login(request);

        assertThat(result.getAccessToken()).isEqualTo(accessToken);
        assertThat(result.getRefreshToken()).isEqualTo(refreshToken);
        then(redisTemplate.opsForValue()).should(times(1)).set("refresh:" + userId, refreshToken, 7, TimeUnit.DAYS);
    }

    @Test
    @DisplayName("로그인 실패 - 이메일 없음")
    void login_fail_email_not_found() {
        LoginRequest request = new LoginRequest(email, password);
        given(userRepository.findByEmail(email)).willReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.EMAIL_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void login_fail_password_mismatch() {
        LoginRequest request = new LoginRequest(email, password);
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(password, encodedPassword)).willReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.NOT_MATCH_PASSWORD.getMessage());
    }

    @Test
    @DisplayName("리프레시 토큰 성공 테스트")
    void refreshToken_success() {
        TokenRefreshRequest request = new TokenRefreshRequest(refreshToken);
        given(jwtUtil.validateToken(refreshToken)).willReturn(true);
        given(jwtUtil.getEmailFromToken(refreshToken)).willReturn(email);
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get("refresh:" + userId)).willReturn(refreshToken);
        given(jwtUtil.createAccessToken(userId, email)).willReturn(accessToken);
        given(jwtUtil.createRefreshToken(userId, email)).willReturn("new-refresh");

        TokenRefreshResponse response = authService.refresh(request);

        assertThat(response.getAccessToken()).isEqualTo(accessToken);
        assertThat(response.getRefreshToken()).isEqualTo("new-refresh");
    }

    @Test
    @DisplayName("리프레시 토큰 실패 - 유효하지 않은 토큰")
    void refreshToken_fail_invalid_token() {
        TokenRefreshRequest request = new TokenRefreshRequest(refreshToken);
        given(jwtUtil.validateToken(refreshToken)).willReturn(false);

        assertThatThrownBy(() -> authService.refresh(request))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.INVALID_REFRESH_TOKEN.getMessage());
    }

    @Test
    @DisplayName("로그아웃 성공 - 블랙리스트 등록")
    void logout_success_with_blacklist() {
        Claims claims = mock(Claims.class);
        given(jwtUtil.getClaims(accessToken)).willReturn(claims);
        given(claims.getExpiration()).willReturn(new java.util.Date(System.currentTimeMillis() + 60000));
        given(redisTemplate.opsForValue()).willReturn(valueOperations);

        authService.logout(userId, accessToken);

        then(redisTemplate).should(times(1)).delete("refresh:" + userId);
        then(redisTemplate.opsForValue()).should(times(1)).set(
                startsWith("blacklist:"), eq("logout"), anyLong(), eq(TimeUnit.MILLISECONDS));
    }
}
