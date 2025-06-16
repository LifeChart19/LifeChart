package org.example.lifechart.domain.simulation.service.calculator;

import lombok.RequiredArgsConstructor;
import org.example.lifechart.domain.goal.entity.Goal;
import org.example.lifechart.domain.simulation.dto.response.MonthlyAchievement;
import org.example.lifechart.domain.simulation.dto.response.MonthlyAssetDto;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SimulationCalculator {

    // 1. 앞으로 모아야 하는 금액
    public long calculateRequiredAmount(long initialAsset, List<Goal> selectedGoals) {
        long totalGoalAmount = selectedGoals.stream()
                .mapToLong(Goal::getTargetAmount)
                .sum();
        return Math.max(0, totalGoalAmount - initialAsset);
    }

    // 2. 목표 달성까지 예상 날짜 반환
    public static YearMonth estimateAchieveMonth(
            double monthlySaving,
            double annualInterestRate,
            double targetAmount,
            YearMonth baseMonth
    ) {
        // a = C * r연 / 2400
        double a = monthlySaving * (annualInterestRate / 2400);

        // b = C * (1 + r연 / 2400)
        double b = monthlySaving * (1 + (annualInterestRate / 2400));

        // c = -B
        double c = -targetAmount;

        // 판별식
        double discriminant = Math.pow(b, 2) - 4 * a * c;
        if (discriminant < 0) {
            throw new IllegalArgumentException("해가 존재하지 않습니다. 입력값을 확인하세요.");
        }

        // 양수 해만 사용 (소수점 올림)
        double n = (-b + Math.sqrt(discriminant)) / (2 * a);
        int monthsToAchieve = (int) Math.ceil(n);

        return baseMonth.plusMonths(monthsToAchieve);
    }

    // 3. 현재 달성률 (%) 계산
    public static double calculateProgressRate(
            double initialAsset,
            double monthlySaving,
            int elapsedMonths,
            double annualInterestRate,
            double targetAmount
    ) {
        // 총 이자 계산 (단리 적금 공식)
        double interest = monthlySaving
                * elapsedMonths * (elapsedMonths + 1) / 2.0
                * (annualInterestRate / 100.0) / 12.0;

        // 누적 자산 계산
        double accumulatedAsset = initialAsset + (monthlySaving * elapsedMonths) + interest;

        // 목표 달성률 계산
        double progressRate = (accumulatedAsset / targetAmount) * 100;

        return Math.min(progressRate, 100.0); // 100% 초과 방지
    }

    // 4. 매달 예상 달성률 리스트 반환
    public static List<MonthlyAchievement> calculateMonthlyProgressRates(
            double monthlySaving,
            double annualInterestRate,
            double targetAmount,
            int totalMonths,
            YearMonth baseMonth // 기준 월 필요
    ) {
        List<MonthlyAchievement> progressList = new ArrayList<>();

        for (int m = 1; m <= totalMonths; m++) {
            double accumulated = monthlySaving * m
                    + monthlySaving * m * (m + 1) / 2.0 * (annualInterestRate / 100.0) / 12.0;

            double progressRate = (accumulated / targetAmount) * 100.0;

            YearMonth month = baseMonth.plusMonths(m - 1); // m개월 차 → baseMonth + (m - 1)

            progressList.add(new MonthlyAchievement(month, (float) progressRate));
        }

        return progressList;
    }

    // 5. 매달 자산 변화 시뮬레이션 (자산 금액, 매달 변화)
// 초기 자산부터 시작하여, 매월 (소득 - 지출)을 반영하고 수익률을 적용
    public static List<MonthlyAssetDto> simulateMonthlyAssetsWithInterest(
            long initialAsset,
            long monthlyIncome,
            long monthlyExpense,
            double annualInterestRate,
            int totalMonths,
            YearMonth baseMonth
    ) {
        List<MonthlyAssetDto> assets = new ArrayList<>();
        double currentAsset = initialAsset;
        double monthlySaving = monthlyIncome - monthlyExpense;
        double monthlyRate = annualInterestRate / 12.0 / 100.0;

        for (int m = 1; m <= totalMonths; m++) {
            double interest = monthlySaving * m * monthlyRate;
            currentAsset = currentAsset + monthlySaving + interest;

            YearMonth currentMonth = baseMonth.plusMonths(m - 1); // 월 단위로 증가
            assets.add(new MonthlyAssetDto(currentMonth, Math.round(currentAsset)));
        }

        return assets;
    }
}