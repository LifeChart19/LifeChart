package org.example.lifechart.domain.user.service;

import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.common.port.SendSqsPort;
import org.example.lifechart.domain.user.dto.SignupRequest;
import org.example.lifechart.domain.user.dto.UserUpdateRequest;
import org.example.lifechart.domain.user.dto.WithdrawalRequest;
import org.example.lifechart.domain.user.entity.User;
import org.example.lifechart.domain.user.port.AccountEventPublisherPort;
import org.example.lifechart.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private AccountEventPublisherPort accountEventPublisherPort;

    @Mock
    private SendSqsPort sendSqsPort;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("회원가입 성공")
    void signup_success() {
        SignupRequest request = new SignupRequest(
                "test@email.com",
                "pass",
                "테스터",
                "nick",
                LocalDate.now(),
                new java.math.BigDecimal("1500000"),
                "MALE",
                "JOB",
                "01012345678"
        );

        given(userRepository.existsByEmail(any())).willReturn(false);
        given(userRepository.existsByNickname(any())).willReturn(false);
        given(passwordEncoder.encode(any())).willReturn("encoded_pw");

        given(userRepository.save(any())).willAnswer(invocation -> {
            User user = invocation.getArgument(0);
            ReflectionTestUtils.setField(user, "createdAt", java.time.LocalDateTime.now());
            return user;
        });

        User result = userService.signup(request);

        assertThat(result.getEmail()).isEqualTo("test@email.com");
        assertThat(result.getPassword()).isEqualTo("encoded_pw");
        assertThat(result.getCreatedAt()).isNotNull();
    }


    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void signup_fail_duplicate_email() {

        given(userRepository.existsByEmail("duplicate@email.com")).willReturn(true);

        CustomException exception = assertThrows(CustomException.class, () ->
                userService.signup(new SignupRequest(
                        "duplicate@email.com",
                        "pw",
                        "name",
                        "nick",
                        LocalDate.now(),
                        new BigDecimal("1500000"),
                        null, null, null))
        );

        assertEquals("이미 존재하는 이메일입니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
    }

    @Test
    @DisplayName("회원가입 실패 - soft delete된 이메일로 재가입 시도")
    void signup_fail_deleted_user_email() {
        given(userRepository.existsByEmail("deleted@email.com")).willReturn(false);
        given(userRepository.existsByEmailAndIsDeletedTrue("deleted@email.com")).willReturn(true);

        CustomException exception = assertThrows(CustomException.class, () ->
                userService.signup(new SignupRequest(
                        "deleted@email.com",
                        "pw",
                        "name",
                        "nick",
                        LocalDate.now(),
                        new BigDecimal("1500000"),
                        null, null, null))
        );

        assertEquals("탈퇴 진행 중인 이메일입니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
    }

    @Test
    @DisplayName("회원가입 실패 - 닉네임 중복")
    void signup_fail_duplicate_nickname() {
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.existsByEmailAndIsDeletedTrue(any())).thenReturn(false);
        when(userRepository.existsByNickname("nickname")).thenReturn(true);

        CustomException exception = assertThrows(CustomException.class, () ->
                userService.signup(new SignupRequest(
                        "duplicate@email.com",
                        "pw",
                        "name",
                        "nickname",
                        LocalDate.now(),
                        new BigDecimal("1500000"),
                        null, null, null))
        );

        assertEquals("이미 존재 하는 닉네임입니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
    }

    @Test
    @DisplayName("회원정보 수정 성공")
    void updateProfile_success() {
        User user = User.builder().id(1L).nickname("oldNick").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByNickname("newNick")).thenReturn(false);

        UserUpdateRequest request = new UserUpdateRequest("newNick", "FEMALE", "DEV", "01099998888",new java.math.BigDecimal("1500000"));
        userService.updateProfile(1L, request);

        assertThat(user.getNickname()).isEqualTo("newNick");
        assertThat(user.getJob()).isEqualTo("DEV");
    }

    @Test
    @DisplayName("회원정보 수정 실패 - 존재하지 않는 유저")
    void updateProfile_fail_user_not_found() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () ->
                userService.updateProfile(1L, new UserUpdateRequest(
                        "nick",
                        "MALE",
                        "DEVELOPER",
                        "0100000000",
                        new BigDecimal("1500000")
        )));

        assertEquals("유저를 찾을 수 없습니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
    }

    @Test
    @DisplayName("회원정보 수정 실패 - 닉네임 중복")
    void updateProfile_fail_duplicate_nickname() {
        User user = User.builder().id(1L).nickname("oldNick").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(userRepository.existsByNickname("myNick")).thenReturn(true);

        CustomException exception = assertThrows(CustomException.class, () ->
                userService.updateProfile(1L, new UserUpdateRequest(
                        "myNick",
                        "MALE",
                        "DEVELOPER",
                        "0100000000",
                        new BigDecimal("1500000")
                )));

        assertEquals("이미 존재 하는 닉네임입니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
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

        CustomException exception = assertThrows(CustomException.class, () ->
                userService.withdraw(1L, new WithdrawalRequest("wrong")));

        assertEquals("비밀번호가 일치하지 않습니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
    }

    @Test
    @DisplayName("회원 탈퇴 실패 - 존재하지 않는 유저")
    void withdraw_fail_user_not_found() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () ->
                userService.withdraw(1234L, new WithdrawalRequest("wrong")));

        assertEquals("유저를 찾을 수 없습니다.", exception.getErrorCode().getReasonHttpStatus().getMessage());
    }
}
