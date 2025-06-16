package org.example.lifechart.domain.user.service;

import org.example.lifechart.domain.user.dto.SignupRequest;
import org.example.lifechart.domain.user.dto.UserUpdateRequest;
import org.example.lifechart.domain.user.dto.WithdrawalRequest;
import org.example.lifechart.domain.user.entity.User;

import java.util.Optional;

public interface UserService {
    User signup(SignupRequest request);
    void updateProfile(Long userId, UserUpdateRequest request);
    void withdraw(Long userId, WithdrawalRequest request);
}
