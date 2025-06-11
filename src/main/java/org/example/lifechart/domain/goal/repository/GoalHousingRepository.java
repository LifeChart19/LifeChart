package org.example.lifechart.domain.goal.repository;

import org.example.lifechart.domain.goal.entity.GoalHousing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalHousingRepository extends JpaRepository<GoalHousing, Long> {
}
