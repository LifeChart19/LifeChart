package org.example.lifechart.security;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class JwtUtil {
    public String createAccessToken(Long userId, String email) {
        return "access-token-for-" + userId;
    }
    public String createRefreshToken(Long userId, String email) {
        return "refresh-token-for-" + userId;
    }
    // 아직 jwt토큰 발급은 구현이 안되어 있습니다. 일단 구조만 짠거라 반환값은 임시로 적어뒀습니다.
}
