package org.example.lifechart.simulation;

import org.example.lifechart.domain.goal.entity.Goal;
import org.example.lifechart.domain.simulation.dto.response.MonthlyAchievement;
import org.example.lifechart.domain.simulation.dto.response.MonthlyAssetDto;
import org.example.lifechart.domain.simulation.service.calculator.SimulationCalculator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.YearMonth;
import java.util.List;

//단리 정기적금 기준.
@ExtendWith(MockitoExtension.class)
public class SimulationCalculatorTest {

    private final SimulationCalculator calculator = new SimulationCalculator();


    private List<Goal> createDummyGoals() {
        return List.of(
                Goal.builder().targetAmount(5000000L).build(),
                Goal.builder().targetAmount(3000000L).build()
        );
    }

    //앞으로 모아야하는 금액 계좌연동
    @Test
    void testCalculateRequiredAmount() {
        long result = calculator.calculateRequiredAmount(2000000, createDummyGoals());
        System.out.println("앞으로 필요한 금액" + result); // 8000000 - 2000000 = 6000000
    }



    //목표금액도 차이가 있긴할거고,
    //이자, 투자수익률(어려움), 추가 지출/수입이 반영되지 않은 순수 저축 공식
    //단리 수익률이 매월 변동하는 경우 ..? 수익률은 매번 일정해야 -> 목표 달성까지 예상 월을 정할 수 있음.
    @Test
    void testEstimateAchieveMonth() {
        YearMonth baseMonth = YearMonth.of(2025, 6);
        YearMonth result = SimulationCalculator.estimateAchieveMonth(5000000, 6, 10000000, baseMonth);
        System.out.println("목표 달성까지 예상 월" +  result);

    }


    //초기자산, 매달 저축액, 경과 개월 수, 연 이율, 목표 금액
    //단리 적금 기준 누적 자산과 목표 달성률
    //1. 총 이자 계산 후 2. 누적 자산 계산 3. 목표 달성률 계산
    @Test
    void testCalculateAchievementRate() {
        float rate = (float) SimulationCalculator.calculateProgressRate(2000000, 500000, 12, 6.0, 10000000);
        System.out.println("달성률" + rate + "%");
    }

    //월:xx 자산: 502,500원, 달성률: 5.03% 로 이런 식으로 반환
    @Test
    void testCalculateMonthlyAchievement() {
        List<MonthlyAchievement> result = SimulationCalculator.calculateMonthlyProgressRates(
                500_000,
                6.0,
                10_000_000,
                5,
                YearMonth.of(2025, 6) // baseMonth는 반드시 필요합니다
        );

        result.forEach(row -> {
            System.out.printf("%s - 달성률: %.2f%%%n", row.getMonth(), row.getAchievementRate());
        });
    }


    //소득, 지출 고정으로 . 연 이율이 주어진다면 매달 자산 변화
    @Test
    void testCalculateMonthlyAssetsOnly() {

        YearMonth baseMonth = YearMonth.of(2025, 6);
        List<MonthlyAssetDto> result = SimulationCalculator.simulateMonthlyAssetsWithInterest(
                1000000,  // 초기 자산
                3000000,  // 월 소득
                2000_000,
                4,
                baseMonth

        );

        result.stream().limit(12).forEach(asset -> System.out.println("단리 적금 기준 매달 자산 변화 :"  + asset));
    }

}
