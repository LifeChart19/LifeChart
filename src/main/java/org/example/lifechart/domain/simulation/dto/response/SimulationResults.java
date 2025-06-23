package org.example.lifechart.domain.simulation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

//사용자에게 필요한 계산필드 반환
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimulationResults {
    //추가 필요 금액
    private Long requiredAmount;

    //예상 개월 수
    private Integer monthsToGoal;

    //목표 대비 얼마나 달성되었는지. 달성률
    private Float currentAchievementRate;

    //달별 예상 달성률
    private List<MonthlyAchievement> monthlyAchievements;

    //매달 자산 가격 변화만 반환
    private List<MonthlyAssetDto> monthlyAssets;
}