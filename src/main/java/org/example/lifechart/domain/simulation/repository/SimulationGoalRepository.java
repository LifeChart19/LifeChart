package org.example.lifechart.domain.simulation.repository;

import org.example.lifechart.domain.goal.entity.Goal;
import org.example.lifechart.domain.simulation.entity.SimulationGoal;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SimulationGoalRepository extends CrudRepository<SimulationGoal, Long> {


    @Query("""
    SELECT sg.goal
    FROM SimulationGoal sg
    WHERE sg.simulation.id = :simulationId
    AND sg.active = true
""")
    List<Goal> findActiveGoalsBySimulationId(@Param("simulationId") Long simulationId);

    List<SimulationGoal> findBySimulationIdAndActiveTrue(Long simulationId);

    @Query("""
    SELECT sg
    FROM SimulationGoal sg
    WHERE sg.goal.id = :goalId
    AND sg.active = true
""")
    List<SimulationGoal> findAllByGoalIdAndActiveTrue(@Param("goalId") Long goalId);

}
