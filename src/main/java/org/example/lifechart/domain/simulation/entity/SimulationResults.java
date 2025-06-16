package org.example.lifechart.domain.simulation.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.lifechart.domain.simulation.dto.response.MonthlyAchievement;
import org.example.lifechart.domain.simulation.dto.response.MonthlyAssetDto;

import java.util.List;

//추후 수정될 수 있음. resutls가 한달 주기로 얼마나 모았는지 보여주는 용도. 다시 필드 작성해야 함.
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