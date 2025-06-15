package org.example.lifechart.domain.user.scheduler;

import org.example.lifechart.domain.user.entity.User;
import org.example.lifechart.domain.user.repository.UserRepository;
import org.example.lifechart.domain.user.service.UserCleanupService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserCleanupServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("30일 지난 soft deleted 유저 삭제")
    void deleteExpiredUsers_removesOldSoftDeletedUsers() {
        // given
        User activeUser = User.builder()
                .email("active@test.com")
                .password("encoded")
                .nickname("active")
                .isDeleted(false)
                .build();

        User recentDeletedUser = User.builder()
                .email("recent@test.com")
                .password("encoded")
                .nickname("recent")
                .isDeleted(true)
                .deletedAt(LocalDateTime.now().minusDays(5))
                .build();

        User expiredDeletedUser = User.builder()
                .email("expired@test.com")
                .password("encoded")
                .nickname("expired")
                .isDeleted(true)
                .deletedAt(LocalDateTime.now().minusDays(31))
                .build();

        userRepository.saveAll(List.of(activeUser, recentDeletedUser, expiredDeletedUser));

        UserCleanupService cleanupService = new UserCleanupService(userRepository);

        // when
        cleanupService.deleteExpiredUsers();

        // then
        List<User> remainingUsers = userRepository.findAll();
        assertThat(remainingUsers).hasSize(2);
        assertThat(remainingUsers).extracting("email")
                .containsExactlyInAnyOrder("active@test.com", "recent@test.com");
    }
}
