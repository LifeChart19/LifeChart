package org.example.lifechart.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.user.port.AccountEventPublisherPort;
import org.example.lifechart.common.port.SendSqsPort;
import org.example.lifechart.domain.user.dto.*;
import org.example.lifechart.domain.user.entity.User;
import org.example.lifechart.domain.user.repository.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SendSqsPort sqsPort;
    private final AccountEventPublisherPort accountEventPublisherPort;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public User signup(SignupRequest request) {
        validateEmailDuplication(request.getEmail());
        validateNicknameDuplication(request.getNickname());

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = User.createFromSignupRequest(request, encodedPassword);

        User savedUser = userRepository.save(user);

        // SNS 발행
        AccountCreatedEvent event = new AccountCreatedEvent(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getName(),
                savedUser.getSalary(),
                savedUser.getCreatedAt().toString()
        );

        accountEventPublisherPort.publishAccountCreatedEvent(event);

        // 알림 SQS 전송
        sqsPort.sendNotification(
                savedUser.getId(),
                "USER_NOTIFICATION",
                "Welcome!",
                "가입을 축하합니다!"
        );

        try {
            eventPublisher.publishEvent(event);
        } catch (Exception e) {
            log.warn("회원가입 Spring 이벤트 발행 실패 : {}", e.getMessage(), e);
        }

        return savedUser;
    }

    private void validateEmailDuplication(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.EXIST_SAME_EMAIL);
        }
        if (userRepository.existsByEmailAndIsDeletedTrue(email)) {
            throw new CustomException(ErrorCode.DELETED_USER_EXISTS);
        }
    }

    private void validateNicknameDuplication(String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw new CustomException(ErrorCode.EXIST_SAME_NICKNAME);
        }
    }

    @Override
    public Long updateProfile(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!user.getNickname().equals(request.getNickname()) &&
                userRepository.existsByNickname(request.getNickname())) {
            throw new CustomException(ErrorCode.EXIST_SAME_NICKNAME);
        }

        user.updateProfile(
                request.getNickname(),
                request.getGender(),
                request.getJob(),
                request.getPhoneNumber(),
                request.getSalary()
        );

        return user.getId();
    }

    @Override
    public UserProfileResponse getProfile(Long userId) {
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return new UserProfileResponse(user);
    }

    @Override
    public UserPublicProfileResponse getUserById(Long userId) {
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return new UserPublicProfileResponse(user);
    }

    @Override
    public Long withdraw(Long userId, WithdrawalRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new CustomException(ErrorCode.NOT_MATCH_PASSWORD);
        }

        user.softDelete();
        return user.getId();
    }

    @Override
    public boolean existsById(Long userId) {
        return userRepository.existsByIdAndIsDeletedFalse(userId);
    }
}
