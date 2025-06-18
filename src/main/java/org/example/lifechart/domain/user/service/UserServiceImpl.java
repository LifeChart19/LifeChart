package org.example.lifechart.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.common.port.AccountEventPublisherPort;
import org.example.lifechart.common.port.SendSqsPort;
import org.example.lifechart.domain.user.dto.*;
import org.example.lifechart.domain.user.entity.User;
import org.example.lifechart.domain.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SendSqsPort sqsPort;
    private final AccountEventPublisherPort accountEventPublisherPort;

    @Override
    public User signup(SignupRequest request) {
        validateEmailDuplication(request.getEmail());
        validateNicknameDuplication(request.getNickname());

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .name(request.getName())
                .nickname(request.getNickname())
                .birthDate(request.getBirthDate())
                .gender(request.getGender())
                .job(request.getJob())
                .phoneNumber(request.getPhoneNumber())
                .role("USER")
                .isDeleted(false)
                .build();

        User savedUser = userRepository.save(user);

        // SNS 발행
        accountEventPublisherPort.publishAccountCreatedEvent(
                new AccountCreatedEvent(
                        savedUser.getId(),
                        savedUser.getEmail(),
                        savedUser.getNickname(),
                        savedUser.getCreatedAt().toString()
                )
        );

        // 알림 SQS 전송
        sqsPort.sendNotification(
                savedUser.getId(),
                "USER_NOTIFICATION",
                "Welcome!",
                "가입을 축하합니다!"
        );

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
                request.getPhoneNumber()
        );

        return user.getId();
    }

    @Override
    public UserProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return new UserProfileResponse(user);
    }

    @Override
    public UserPublicProfileResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
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
}
