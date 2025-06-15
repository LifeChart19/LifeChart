package org.example.lifechart.domain.user.service;

import org.example.lifechart.domain.user.dto.SignupRequest;
import org.example.lifechart.domain.user.dto.WithdrawalRequest;
import org.example.lifechart.domain.user.entity.User;
import org.example.lifechart.domain.user.repository.UserRepository;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.common.enums.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원가입 성공")
    void signupSuccess() {
        SignupRequest request = SignupRequest.builder()
                .email("test@email.com")
                .password("password123")
                .nickname("nickname")
                .birthDate(LocalDate.of(2000, 1, 1))
                .gender("MALE")
                .job("STUDENT")
                .phoneNumber("01012345678")
                .build();

        User savedUser = userService.signup(request);

        assertNotNull(savedUser.getId());
        assertEquals("test@email.com", savedUser.getEmail());
        assertTrue(passwordEncoder.matches("password123", savedUser.getPassword()));
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void signupFail_DuplicateEmail() {
        SignupRequest request = SignupRequest.builder()
                .email("test@email.com")
                .password("password123")
                .nickname("nickname")
                .birthDate(LocalDate.of(2000, 1, 1))
                .gender("MALE")
                .job("STUDENT")
                .phoneNumber("01012345678")
                .build();
        userService.signup(request);

        SignupRequest duplicate = SignupRequest.builder()
                .email("test@email.com")
                .password("password456")
                .nickname("othernick")
                .birthDate(LocalDate.of(2001, 2, 2))
                .gender("FEMALE")
                .job("WORKER")
                .phoneNumber("01000000000")
                .build();

        CustomException exception = assertThrows(CustomException.class, () -> userService.signup(duplicate));
        assertEquals(ErrorCode.EXIST_SAME_EMAIL, exception.getErrorCode());
    }

    @Test
    @DisplayName("회원가입 실패 - 닉네임 중복")
    void signupFail_DuplicateNickname() {
        SignupRequest request = SignupRequest.builder()
                .email("email1@test.com")
                .password("password123")
                .nickname("dupNick")
                .birthDate(LocalDate.of(2000, 1, 1))
                .gender("MALE")
                .job("STUDENT")
                .phoneNumber("01012345678")
                .build();
        userService.signup(request);

        SignupRequest duplicate = SignupRequest.builder()
                .email("email2@test.com")
                .password("password456")
                .nickname("dupNick")
                .birthDate(LocalDate.of(1999, 12, 31))
                .gender("FEMALE")
                .job("WORKER")
                .phoneNumber("01099999999")
                .build();

        CustomException exception = assertThrows(CustomException.class, () -> userService.signup(duplicate));
        assertEquals(ErrorCode.EXIST_SAME_NICKNAME, exception.getErrorCode());
    }

    @Test
    @DisplayName("회원 탈퇴 성공")
    void withdrawalSuccess() {
        SignupRequest request = SignupRequest.builder()
                .email("withdraw@test.com")
                .password("securePass")
                .nickname("withdrawNick")
                .birthDate(LocalDate.of(1995, 5, 5))
                .gender("MALE")
                .job("DEV")
                .phoneNumber("01055556666")
                .build();
        User user = userService.signup(request);

        WithdrawalRequest withdrawalRequest = new WithdrawalRequest("securePass");
        userService.withdraw(user.getId(), withdrawalRequest);

        User deletedUser = userRepository.findById(user.getId()).orElseThrow();
        assertTrue(deletedUser.getIsDeleted());
        assertNotNull(deletedUser.getDeletedAt());
    }

    @Test
    @DisplayName("회원 탈퇴 실패 - 비밀번호 불일치")
    void withdrawalFail_WrongPassword() {
        SignupRequest request = SignupRequest.builder()
                .email("failwithdraw@test.com")
                .password("rightPass")
                .nickname("failNick")
                .birthDate(LocalDate.of(1992, 3, 3))
                .gender("MALE")
                .job("OFFICE")
                .phoneNumber("01033334444")
                .build();
        User user = userService.signup(request);

        WithdrawalRequest wrong = new WithdrawalRequest("wrongPass");

        CustomException exception = assertThrows(CustomException.class, () -> userService.withdraw(user.getId(), wrong));
        assertEquals(ErrorCode.NOT_MATCH_PASSWORD, exception.getErrorCode());
    }
}
