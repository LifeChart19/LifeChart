package org.example.lifechart.domain.simulation.dto.response;

import lombok.*;
import org.example.lifechart.domain.simulation.entity.Simulation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


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


    //아무 값 주지 않은 경우 빈 리스트 default설젇
    @Builder.Default
    List<Long> goalIds = new ArrayList<>();

     //계산에 필요한 필드
//    @Convert(converter = SimulationParamsConverter.class)
//    @Column(columnDefinition = "json")
//    private SimulationParams params;

    //계산결과
//    @Convert(converter = SimulationParamsConverter.class)
//    @Column(columnDefinition = "json")
//    private SimulationResults results;

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

    //이건 조회시.
    public static BaseSimulationResponseDto dto(
            Simulation simulation
    ) {
        return BaseSimulationResponseDto.builder()
                .simulationId(simulation.getId())
                .goalIds(simulation.getSimulationGoals().stream()
                        .map(simGoal -> simGoal.getGoal().getId())
                        .collect(Collectors.toList()))
                .userNickname(simulation.getUser().getNickname())
                .title(simulation.getTitle())
                .baseDate(simulation.getBaseDate())
                .requiredAmount(simulation.getRequiredAmount())
                .monthsToGoal(simulation.getMonthsToGoal())
                .currentAchievementRate(simulation.getCurrentAchievementRate())
                .monthlyAchievements(simulation.getMonthlyAchievements())
                .monthlyAssets(simulation.getMonthlyAssets())
                .build();
    }

}
