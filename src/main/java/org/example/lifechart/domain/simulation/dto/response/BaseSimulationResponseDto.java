package org.example.lifechart.domain.simulation.dto.response;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import lombok.*;
import org.example.lifechart.domain.simulation.converter.SimulationParamsConverter;
import org.example.lifechart.domain.simulation.entity.Simulation;
import org.example.lifechart.domain.simulation.entity.SimulationParams;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
//final???
//공통 부모 DTO
public class BaseSimulationResponseDto {

    private Long simulationId;

    private String userNickname;

    //제목
    private String title;

    //기준일 사용자 설정 가능
    private LocalDateTime baseDate;

    //최초 자산
    private Long initialAsset;

    //수입
    private Long monthlyIncome;

    //지출
    private Long monthlyExpense;

    //시뮬레이션을 2개이상 돌리는 경우
    List<Long> goalIds = new ArrayList<>();

    //시뮬레이션을 돌릴 때 사용자가 입력하거나, 시스템이 자동으로 구성한 입력값들이라 현재 Object로 남겨놓으신거 맞은지
    @Convert(converter = SimulationParamsConverter.class)
    @Column(columnDefinition = "json")
    private SimulationParams params;

    @Convert(converter = SimulationParamsConverter.class)
    @Column(columnDefinition = "json")
    private SimulationParams results;


    public static BaseSimulationResponseDto toDto(Simulation simulation) {
        return BaseSimulationResponseDto.builder()
                .simulationId(simulation.getId())
                .userNickname(simulation.getUser().getNickname())
                .title(simulation.getTitle())
                .baseDate(simulation.getBaseDate())
                .initialAsset(simulation.getInitialAsset())
                .monthlyIncome(simulation.getMonthlyIncome())
                .monthlyExpense(simulation.getMonthlyExpense())
                //simGoal(List<SimulationGoal>) -> goal -> goalId의 각 요소)
                .goalIds(simulation.getSimulationGoals().stream()
                        .map(simGoal -> simGoal.getGoal().getId())
                        .toList())
                .params(simulation.getParams())
                .results(simulation.getResults())
                .build();
    }

}
