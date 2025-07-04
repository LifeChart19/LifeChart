package org.example.lifechart.domain.simulation.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.lifechart.domain.goal.entity.Goal;

import java.time.LocalDateTime;

@Builder
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SimulationGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "simulation_id")
    @Setter(AccessLevel.PROTECTED)
    private Simulation simulation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id")
    private Goal goal;

    private LocalDateTime linkedAt;

    private LocalDateTime unlinkedAt;

    private boolean active;

}

