package org.example.lifechart.domain.simulation.dto.response;

//@SuperBuilder
//@Getter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@AllArgsConstructor(access = AccessLevel.PROTECTED)
//public class SimulationGoalResponseDto extends BaseSimulationResponseDto{
//
//    private Long goalId;
//    private boolean isActive;
//    private LocalDateTime linkedAt;

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
//}