package org.example.lifechart.domain.simulation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SimulationGoalDto extends BaseSimulationResponseDto {

    private Long simulationId;   // Simulation PK
    private Long goalId;         // Goal PK
    private boolean isActive;    // 활성화 여부
    private LocalDateTime linkedAt; // 연결 시점

//    public static SimulationGoalResponseDto toDto(SimulationGoal simulationGoal, Simulation simulation) {
//        return SimulationGoalResponseDto.builder()
//                .simulationId(simulation.getId())
//                .goalId(simulationGoal.getGoal().getId())
//                .userNickname(simulation.getUser().getNickname())
//                .title(simulation.getTitle())
//                .baseDate(simulation.getBaseDate())
//                .initialAsset(simulation.getInitialAsset())
//                .monthlyIncome(simulation.getMonthlyIncome())
//                .monthlyExpense(simulation.getMonthlyExpense())
//                .goalIds(simulation.getSimulationGoals().stream()
//                        .map(simGoal -> simGoal.getGoal().getId())
//                        .toList())
//                .params(simulation.getParams())
//                .results(simulation.getResults())
//                .isActive(simulationGoal.isActive())
//                .linkedAt(simulationGoal.getLinkedAt())
//                .build();
//    }

}
