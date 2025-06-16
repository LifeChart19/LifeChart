package org.example.lifechart.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.example.lifechart.common.port.SendSqsPort;
import org.example.lifechart.domain.user.dto.SignupRequest;
import org.example.lifechart.domain.user.dto.UserUpdateRequest;
import org.example.lifechart.domain.user.dto.WithdrawalRequest;
import org.example.lifechart.domain.user.entity.User;
import org.example.lifechart.domain.user.repository.UserRepository;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.common.enums.ErrorCode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SendSqsPort sqsPort;


    @Override
    public User signup(SignupRequest request) {

        validateEmailDuplication(request.getEmail());
        validateNicknameDuplication(request.getNickname());

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 유저 생성 및 저장
        User user = User.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .nickname(request.getNickname())
                .birthDate(request.getBirthDate())
                .gender(request.getGender())
                .job(request.getJob())
                .phoneNumber(request.getPhoneNumber())
                .role("USER")
                .isDeleted(false)
                .build();

        User savedUser = userRepository.save(user);

        sqsPort.sendNotification(savedUser.getId(), "USER_NOTIFICATION", "Welcome!", "가입을 축하합니다!    ");

        return savedUser;
    }

    private void validateEmailDuplication(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.EXIST_SAME_EMAIL);
        }
        if (userRepository.existsByEmailAndIsDeletedTrue(email)) {
            throw new CustomException(ErrorCode.DELETED_USER_EXISTS);  // 탈퇴 중 이메일 예외
        }
    }

    private void validateNicknameDuplication(String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw new CustomException(ErrorCode.EXIST_SAME_NICKNAME);
        }
    }


    @Override
    @Transactional
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
    @Transactional
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
