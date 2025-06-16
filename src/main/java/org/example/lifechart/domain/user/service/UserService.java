package org.example.lifechart.domain.user.service;

import org.example.lifechart.domain.user.dto.SignupRequest;
import org.example.lifechart.domain.user.dto.UserUpdateRequest;
import org.example.lifechart.domain.user.dto.WithdrawalRequest;
import org.example.lifechart.domain.user.entity.User;

public interface UserService {
    User signup(SignupRequest request);
    Long updateProfile(Long userId, UserUpdateRequest request);
    Long withdraw(Long userId, WithdrawalRequest request);
}
