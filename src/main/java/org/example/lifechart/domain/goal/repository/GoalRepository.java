package org.example.lifechart.domain.goal.repository;

import java.util.List;
import java.util.Optional;

import org.example.lifechart.domain.goal.entity.Goal;
import org.example.lifechart.domain.goal.enums.Status;
import org.example.lifechart.domain.shareGoal.repository.CustomShareGoalRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long>, CustomShareGoalRepository {
    // userId 기준으로 Goal 목록 조회 (예: 특정 사용자의 모든 목표 조회)
    List<Goal> findByUserId(Long userId);

    // goalId + userId 조건으로 Goal 조회 (권한 검증용으로 자주 사용)
    Optional<Goal> findByIdAndUserId(Long id, Long userId);
    // 추가적인 쿼리 메서드가 필요하면 여기에 선언하시면 됩니다.
    Optional<Goal> findByIdAndStatus(Long id, Status status);
}