package org.example.lifechart.domain.user.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.lifechart.domain.user.service.UserCleanupService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserCleanupScheduler {

    private final UserCleanupService userCleanupService;

    @Scheduled(cron = "0 0 2 * * ?") // 매일 새벽 2시
    public void runUserCleanupJob() {
        log.info("[스케줄러] 탈퇴 유저 정리 작업 시작");
        userCleanupService.deleteExpiredUsers();
        log.info("[스케줄러] 탈퇴 유저 정리 작업 종료");
    }
}
