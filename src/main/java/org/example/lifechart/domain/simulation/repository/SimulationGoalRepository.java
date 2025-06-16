package org.example.lifechart.domain.simulation.repository;

import org.example.lifechart.domain.simulation.entity.Simulation;
import org.example.lifechart.domain.simulation.entity.SimulationGoal;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SimulationGoalRepository extends CrudRepository<SimulationGoal, Long> {

    @Query("""
    SELECT DISTINCT sg.simulation
    FROM SimulationGoal sg
    WHERE sg.goal.id = :goalId
""")
    List<Simulation> findDistinctSimulationsByGoalId(@Param("goalId") Long goalId);

    @Query("""
    SELECT COUNT(sg)
    FROM SimulationGoal sg
    WHERE sg.simulation.id = :simulationId
      AND sg.isActive = true
""")
    long countActiveGoalsBySimulationId(@Param("simulationId") Long simulationId);

    List<SimulationGoal> findBySimulationIdAndActiveTrue(Long simulationId);
}

//→ GoalId로만 필터링 → 자동으로 해당 Goal과 연결된 Simulation들이 다 조회됨 → 별도 로직 불필요.