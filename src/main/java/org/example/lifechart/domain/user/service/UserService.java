package org.example.lifechart.domain.user.service;

import org.example.lifechart.domain.user.dto.*;
import org.example.lifechart.domain.user.entity.User;

public interface UserService {
    User signup(SignupRequest request);
    Long updateProfile(Long userId, UserUpdateRequest request);
    Long withdraw(Long userId, WithdrawalRequest request);
    UserProfileResponse getProfile(Long userId); // 내 정보 조회
    UserPublicProfileResponse getUserById(Long userId); // 타인 정보 (id 기준)
}
