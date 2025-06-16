package org.example.lifechart.domain.user.scheduler;

import org.example.lifechart.domain.user.entity.User;
import org.example.lifechart.domain.user.repository.UserRepository;
import org.example.lifechart.domain.user.service.UserCleanupService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;

class UserCleanupServiceTest {

    @Test
    @DisplayName("30일 지난 soft deleted 유저 삭제")
    void deleteExpiredUsers_removesOldSoftDeletedUsers() {
        // given
        UserRepository userRepository = mock(UserRepository.class);

        User expiredDeletedUser = User.builder()
                .email("expired@test.com")
                .password("encoded")
                .nickname("expired")
                .isDeleted(true)
                .deletedAt(LocalDateTime.now().minusDays(31))
                .build();

        when(userRepository.findAllByIsDeletedTrueAndDeletedAtBefore(any()))
                .thenReturn(List.of(expiredDeletedUser));

        UserCleanupService cleanupService = new UserCleanupService(userRepository);

        // when
        cleanupService.deleteExpiredUsers();

        // then
        verify(userRepository, times(1)).deleteAll(List.of(expiredDeletedUser));
    }
}
