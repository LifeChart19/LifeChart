package org.example.lifechart.common.util;

import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.security.CustomUserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.common.enums.ErrorCode;

public class AuthUtil {
    // 현재 로그인한 유저 id 반환 (예: SecurityContext, Principal 등에서)
    public static Long getCurrentUserId() {
        // 예시: SecurityContextHolder에서 CustomUserPrincipal로 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException(ErrorCode.NO_AUTHORIZATION);
        }
        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();
        return principal.getUserId();
    }

    // 파라미터로 받은 userId와 현재 유저 id가 같은지 체크
    public static void validateUserAccess(Long userId) {
        if (!userId.equals(getCurrentUserId())) {
            throw new CustomException(ErrorCode.NO_AUTHORIZATION);
        }
    }
}