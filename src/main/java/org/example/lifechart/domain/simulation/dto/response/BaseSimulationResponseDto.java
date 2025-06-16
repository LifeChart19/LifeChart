package org.example.lifechart.domain.simulation.dto.response;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import lombok.*;
import org.example.lifechart.domain.simulation.converter.SimulationParamsConverter;
import org.example.lifechart.domain.simulation.entity.Simulation;
import org.example.lifechart.domain.simulation.entity.SimulationResults;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
//final???
//공통 부모 DTO
public class BaseSimulationResponseDto {
    //기본 정보
    private Long simulationId;

    private String userNickname;

    //제목
    private String title;

    //기준일 사용자 설정 가능
    private LocalDate baseDate;

    //시뮬레이션을 2개이상 돌리는 경우
    //아무 값 주지 않은 경우 빈 리스트 default
    @Builder.Default
    List<Long> goalIds = new ArrayList<>();

    //시뮬레이션을 돌릴 때 사용자가 입력하거나, 시스템이 자동으로 구성한 입력값들이라 현재 Object로 남겨놓으신거 맞은지
    @Convert(converter = SimulationParamsConverter.class)
    @Column(columnDefinition = "json")
    private SimulationParams params;

    //계산결과
    @Convert(converter = SimulationParamsConverter.class)
    @Column(columnDefinition = "json")
    private SimulationResults results;

    //앞으로 모아야 할 금액
    private Long requiredAmount;

    //목표 달성까지 예상 개월 수
    private Integer monthsToGoal;

    //현재 달성률
    private Float currentAchievementRate;

    //매달 달성 상황 리스트
    private List<MonthlyAchievement> monthlyAchievements;

    //매달 자산 가격 변화
    private List<MonthlyAssetDto> monthlyAssets;

    public static BaseSimulationResponseDto of(
            Simulation simulation,
            List<Long> goalIds,
            SimulationResults results
    ) {
        return BaseSimulationResponseDto.builder()
                .simulationId(simulation.getId())
                .userNickname(simulation.getUser().getNickname())
                .title(simulation.getTitle())
                .baseDate(simulation.getBaseDate())
                .goalIds(goalIds)
                .params(simulation.getParams())
                .requiredAmount(results.getRequiredAmount())
                .monthsToGoal(results.getMonthsToGoal())
                .currentAchievementRate(results.getCurrentAchievementRate())
                .monthlyAchievements(results.getMonthlyAchievements())
                .monthlyAssets(results.getMonthlyAssets())
                .build();
    }
    public static BaseSimulationResponseDto dto(
            Simulation simulation,
            SimulationResults results
    ) {
        return BaseSimulationResponseDto.builder()
                .simulationId(simulation.getId())
                .userNickname(simulation.getUser().getNickname())
                .title(simulation.getTitle())
                .baseDate(simulation.getBaseDate())
                .params(simulation.getParams())
                .requiredAmount(results.getRequiredAmount())
                .monthsToGoal(results.getMonthsToGoal())
                .currentAchievementRate(results.getCurrentAchievementRate())
                .monthlyAchievements(results.getMonthlyAchievements())
                .monthlyAssets(results.getMonthlyAssets())
                .build();
    }

}
