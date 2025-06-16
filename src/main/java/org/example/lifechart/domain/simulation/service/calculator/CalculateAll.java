package org.example.lifechart.domain.simulation.service.calculator;

import lombok.RequiredArgsConstructor;
import org.example.lifechart.domain.goal.entity.Goal;
import org.example.lifechart.domain.simulation.dto.response.MonthlyAchievement;
import org.example.lifechart.domain.simulation.dto.response.MonthlyAssetDto;
import org.example.lifechart.domain.simulation.entity.SimulationResults;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CalculateAll {

    //simualtionResults dto객체로 변환하기 위해 목표리스트 바탕으로 시뮬레이션 결과 계산해야함.
    private final SimulationCalculator calculator;

    public SimulationResults calculate(
            long initialAsset,
            long monthlyIncome,
            long monthlyExpense,
            Long monthlySaving, // null이면 자동계산
            double annualInterestRate,
            int elapsedMonths,
            int totalMonths,
            LocalDate baseDate,
            List<Goal> goals
    ) {
        //연도와 월만 따고, localdate타입으로
        YearMonth baseMonth = YearMonth.from(baseDate);
        //목표를 기반으로 필요한 전체 금액 계산.
        long requiredAmount = calculator.calculateRequiredAmount(initialAsset, goals);

        //월 저축액이 주어지면 사용하고, 없다면 수입-지출
        double saving = (monthlySaving != null) ? monthlySaving : (monthlyIncome - monthlyExpense);

        //목표달성시점을 예측
        YearMonth targetMonth = SimulationCalculator.estimateAchieveMonth(
                saving, annualInterestRate, requiredAmount, baseMonth
        );
        //baseMonth부터 targetMonth까지의 개월 수 계산
        //baseMonth, targetMonth가 usermont타입이라 temporalunit 형태로 사용 -
        int monthsToGoal = (int) baseMonth.until(targetMonth, ChronoUnit.MONTHS);

        //현재 달성률계산
        double progressRate = SimulationCalculator.calculateProgressRate(
                initialAsset, saving, elapsedMonths, annualInterestRate, requiredAmount
        );

        //매달 달성률 계싼
        List<MonthlyAchievement> monthlyAchievements = SimulationCalculator.calculateMonthlyProgressRates(
                saving, annualInterestRate, requiredAmount, totalMonths,baseMonth
        );

        //매달 자산 추이
        List<MonthlyAssetDto> monthlyAssets = SimulationCalculator.simulateMonthlyAssetsWithInterest(
                initialAsset, monthlyIncome, monthlyExpense, annualInterestRate, totalMonths, baseMonth
        );

        return SimulationResults.builder()
                .requiredAmount(requiredAmount)
                .monthsToGoal(monthsToGoal)
                .currentAchievementRate((float) progressRate)
                .monthlyAchievements(monthlyAchievements)
                .monthlyAssets(monthlyAssets) // List<Double> 또는 List<MonthlyAssetDto>
                .build();
    }
}