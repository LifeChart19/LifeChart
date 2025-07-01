package org.example.lifechart.domain.user.service;

import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.user.dto.SignupRequest;
import org.example.lifechart.domain.user.dto.UserUpdateRequest;
import org.example.lifechart.domain.user.dto.WithdrawalRequest;
import org.example.lifechart.domain.user.entity.User;
import org.example.lifechart.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("회원가입 성공")
    void signup_success() {
        SignupRequest request = new SignupRequest("test@email.com", "pass", "테스터","nick", LocalDate.now(), "MALE", "JOB", "01012345678");

        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.existsByNickname(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encoded_pw");
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.signup(request);

        assertThat(result.getEmail()).isEqualTo("test@email.com");
        assertThat(result.getPassword()).isEqualTo("encoded_pw");
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void signup_fail_duplicate_email() {
        when(userRepository.existsByEmail("duplicate@email.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.signup(new SignupRequest("duplicate@email.com", "pw","테스터", "nick", LocalDate.now(), null, null, null)))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.EXIST_SAME_EMAIL.getMessage());
    }

    @Test
    @DisplayName("회원가입 실패 - soft delete된 이메일로 재가입 시도")
    void signup_fail_deleted_user_email() {
        when(userRepository.existsByEmail("deleted@email.com")).thenReturn(false);
        when(userRepository.existsByEmailAndIsDeletedTrue("deleted@email.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.signup(new SignupRequest("deleted@email.com", "pw", "테스터","nick", LocalDate.now(), null, null, null)))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.DELETED_USER_EXISTS.getMessage());
    }

    @Test
    @DisplayName("회원가입 실패 - 닉네임 중복")
    void signup_fail_duplicate_nickname() {
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.existsByEmailAndIsDeletedTrue(any())).thenReturn(false);
        when(userRepository.existsByNickname("nickname")).thenReturn(true);

        assertThatThrownBy(() -> userService.signup(new SignupRequest("email@test.com", "pw", "테스터","nickname", LocalDate.now(), null, null, null)))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.EXIST_SAME_NICKNAME.getMessage());
    }

    @Test
    @DisplayName("회원정보 수정 성공")
    void updateProfile_success() {
        User user = User.builder().id(1L).nickname("oldNick").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByNickname("newNick")).thenReturn(false);

        UserUpdateRequest request = new UserUpdateRequest("newNick", "FEMALE", "DEV", "01099998888");
        userService.updateProfile(1L, request);

        assertThat(user.getNickname()).isEqualTo("newNick");
        assertThat(user.getJob()).isEqualTo("DEV");
    }

    @Test
    @DisplayName("회원정보 수정 실패 - 존재하지 않는 유저")
    void updateProfile_fail_user_not_found() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateProfile(999L, new UserUpdateRequest("nick", null, null, null)))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("회원정보 수정 실패 - 닉네임 중복")
    void updateProfile_fail_duplicate_nickname() {
        User user = User.builder().id(1L).nickname("myNick").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByNickname("newNick")).thenReturn(true);

        assertThatThrownBy(() -> userService.updateProfile(1L, new UserUpdateRequest("newNick", null, null, null)))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.EXIST_SAME_NICKNAME.getMessage());
    }

    @Test
    @DisplayName("회원 탈퇴 성공")
    void withdraw_success() {
        User user = User.builder().id(1L).password("encoded").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pw", "encoded")).thenReturn(true);

        userService.withdraw(1L, new WithdrawalRequest("pw"));
        assertThat(user.getIsDeleted()).isTrue();
    }

    @Test
    @DisplayName("회원 탈퇴 실패 - 비밀번호 불일치")
    void withdraw_fail_wrong_password() {
        User user = User.builder().id(1L).password("encoded").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        assertThatThrownBy(() -> userService.withdraw(1L, new WithdrawalRequest("wrong")))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.NOT_MATCH_PASSWORD.getMessage());
    }

    @Test
    @DisplayName("회원 탈퇴 실패 - 존재하지 않는 유저")
    void withdraw_fail_user_not_found() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.withdraw(1234L, new WithdrawalRequest("pw")))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());
    }
}
