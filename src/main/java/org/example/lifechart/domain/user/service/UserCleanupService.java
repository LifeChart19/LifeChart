package org.example.lifechart.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.example.lifechart.domain.user.entity.User;
import org.example.lifechart.domain.user.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

// 스케쥴러 관련 로직은 파일을 따로 분리하는게 좋다고 하네요.
@Service
@RequiredArgsConstructor
public class UserCleanupService {

    private final UserRepository userRepository;

    // 매일 새벽 2시에 실행 (cron: 초 분 시 일 월 요일)
    @Scheduled(cron = "0 0 2 * * ?")
    public void deleteExpiredUsers() {
        LocalDateTime deadline = LocalDateTime.now().minusDays(30); // 테스트시에는 1분으로 변경후 테스트가능
        List<User> usersToDelete = userRepository.findAllByIsDeletedTrueAndDeletedAtBefore(deadline);

        if (!usersToDelete.isEmpty()) {
            userRepository.deleteAll(usersToDelete);
            System.out.println("Deleted users count: " + usersToDelete.size());
        }
    }
}
