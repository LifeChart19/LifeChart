package org.example.lifechart.domain.user.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.example.lifechart.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    boolean existsByEmailAndIsDeletedTrue(String email);
    List<User> findAllByIsDeletedTrueAndDeletedAtBefore(LocalDateTime deadline);// 30일 넘은 soft deleted 유저 찾기
    Optional<User> findByIdAndDeletedAtIsNull(Long userId);
}


