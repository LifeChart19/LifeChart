package org.example.lifechart.domain.simulation.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.lifechart.domain.goal.entity.Goal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SimulationGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long simulationGoalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "simulation_id")
    private Simulation simulation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id")
    private Goal goal;

    private boolean isActive;

    private LocalDateTime linkedAt;

    private LocalDateTime unlinkedAt;
}
