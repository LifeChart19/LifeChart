package org.example.lifechart.domain.user;

import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.user.dto.SignupRequest;
import org.example.lifechart.domain.user.dto.UserUpdateRequest;
import org.example.lifechart.domain.user.dto.WithdrawalRequest;
import org.example.lifechart.domain.user.entity.User;
import org.example.lifechart.domain.user.repository.UserRepository;
import org.example.lifechart.domain.user.service.UserCleanupService;
import org.example.lifechart.domain.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class UserIntegrationTest {

    @Autowired UserService userService;
    @Autowired UserRepository userRepository;
    @Autowired UserCleanupService userCleanupService;
    @Autowired PasswordEncoder passwordEncoder;

    // 1. 회원가입 성공/실패 케이스
    @Test
    @DisplayName("회원가입 성공/이메일 중복/닉네임 중복/soft delete된 이메일 재가입 예외")
    void signup_cases() {
        // 1-1. 정상 가입
        SignupRequest req1 = SignupRequest.builder()
                .email("user1@email.com")
                .password("pw1234")
                .nickname("nick1")
                .birthDate(LocalDate.of(2000,1,1))
                .gender("MALE")
                .job("STUDENT")
                .phoneNumber("010-1111-2222")
                .build();
        User user1 = userService.signup(req1);
        assertThat(user1.getEmail()).isEqualTo("user1@email.com");

        // 1-2. 이메일 중복
        SignupRequest dupEmail = SignupRequest.builder()
                .email("user1@email.com")
                .password("pw9999")
                .nickname("othernick")
                .birthDate(LocalDate.of(1999,1,1))
                .build();
        assertThrows(CustomException.class, () -> userService.signup(dupEmail));

        // 1-3. 닉네임 중복
        SignupRequest dupNick = SignupRequest.builder()
                .email("user2@email.com")
                .password("pw1111")
                .nickname("nick1")
                .birthDate(LocalDate.of(1998,1,1))
                .build();
        assertThrows(CustomException.class, () -> userService.signup(dupNick));

        // 1-4. soft delete된 이메일로 재가입
        User softDeleted = userRepository.findByEmail("user1@email.com").orElseThrow();
        softDeleted.softDelete();
        userRepository.save(softDeleted);

        SignupRequest rejoinDeleted = SignupRequest.builder()
                .email("user1@email.com")
                .password("pw2222")
                .nickname("newNick")
                .birthDate(LocalDate.of(2002,2,2))
                .build();
        assertThrows(CustomException.class, () -> userService.signup(rejoinDeleted));
    }

    // 2. 회원정보 수정 케이스
    @Test
    @DisplayName("회원정보 수정 성공/닉네임 중복/존재하지 않는 유저")
    void updateProfile_cases() {
        // 정상 회원가입
        User user = userService.signup(SignupRequest.builder()
                .email("profile@test.com").password("pw1234").nickname("nick100").birthDate(LocalDate.of(2000,1,1)).build());

        // 2-1. 성공
        UserUpdateRequest updateRequest = new UserUpdateRequest("newNick100", "FEMALE", "ENGINEER", "010-9999-0000");
        userService.updateProfile(user.getId(), updateRequest);
        User updated = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updated.getNickname()).isEqualTo("newNick100");

        // 2-2. 닉네임 중복
        userService.signup(SignupRequest.builder()
                .email("profile2@test.com").password("pw2222").nickname("dupNick").birthDate(LocalDate.of(1998,2,2)).build());
        UserUpdateRequest dupNick = new UserUpdateRequest("dupNick", "FEMALE", "STUDENT", "010-1111-9999");
        assertThrows(CustomException.class, () -> userService.updateProfile(user.getId(), dupNick));

        // 2-3. 존재하지 않는 유저
        UserUpdateRequest req = new UserUpdateRequest("noone", "M", null, null);
        assertThrows(CustomException.class, () -> userService.updateProfile(987654L, req));
    }

    // 3. 회원 탈퇴 케이스
    @Test
    @DisplayName("회원 탈퇴 성공/비밀번호 불일치/이미 삭제/존재하지 않는 유저")
    void withdraw_cases() {
        User user = userService.signup(SignupRequest.builder()
                .email("del@test.com").password("delpw").nickname("delNick").birthDate(LocalDate.of(1990,1,1)).build());

        // 3-1. 성공
        userService.withdraw(user.getId(), new WithdrawalRequest("delpw"));
        User withdrawn = userRepository.findById(user.getId()).orElseThrow();
        assertThat(withdrawn.getIsDeleted()).isTrue();

        // 3-2. 비밀번호 불일치
        User user2 = userService.signup(SignupRequest.builder()
                .email("wrongpw@test.com").password("pwreal").nickname("wpNick").birthDate(LocalDate.of(1980,1,1)).build());
        assertThrows(CustomException.class, () -> userService.withdraw(user2.getId(), new WithdrawalRequest("wrongpw")));

        // 3-3. 이미 삭제된 유저
        user2.softDelete();
        userRepository.save(user2);
        assertThrows(CustomException.class, () -> userService.withdraw(user2.getId(), new WithdrawalRequest("pwreal")));

        // 3-4. 존재하지 않는 유저
        assertThrows(CustomException.class, () -> userService.withdraw(123456L, new WithdrawalRequest("anypw")));
    }

    // 4. soft-delete 후 30일 경과 유저 완전 삭제
    @Test
    @DisplayName("30일 경과한 soft-deleted 유저만 완전 삭제")
    void cleanup_cases() {
        // active, 최근 soft-delete, 31일 지난 soft-delete
        User activeUser = User.builder()
                .email("active@test.com").password("pw").nickname("active").isDeleted(false).birthDate(LocalDate.now()).build();

        User recentDeleted = User.builder()
                .email("recent@test.com").password("pw").nickname("recent").isDeleted(true)
                .deletedAt(LocalDateTime.now().minusDays(10)).birthDate(LocalDate.now()).build();

        User expiredDeleted = User.builder()
                .email("expired@test.com").password("pw").nickname("expired").isDeleted(true)
                .deletedAt(LocalDateTime.now().minusDays(31)).birthDate(LocalDate.now()).build();

        userRepository.saveAll(List.of(activeUser, recentDeleted, expiredDeleted));
        userCleanupService.deleteExpiredUsers();

        List<User> remaining = userRepository.findAll();
        assertThat(remaining).hasSize(2);
        assertThat(remaining).extracting("email")
                .containsExactlyInAnyOrder("active@test.com", "recent@test.com");
    }
}
