package org.example.lifechart.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.example.lifechart.domain.user.entity.User;
import org.example.lifechart.domain.user.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserCleanupService {

    private final UserRepository userRepository;

    public void deleteExpiredUsers() {
        LocalDateTime deadline = LocalDateTime.now().minusDays(30);
        List<User> usersToDelete = userRepository.findAllByIsDeletedTrueAndDeletedAtBefore(deadline);

        if (!usersToDelete.isEmpty()) {
            userRepository.deleteAll(usersToDelete);
            System.out.println("Deleted users count: " + usersToDelete.size());
        }
    }
}
