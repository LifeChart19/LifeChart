package org.example.lifechart.domain.simulation.service.calculator;

import lombok.RequiredArgsConstructor;
import org.example.lifechart.domain.goal.entity.Goal;
import org.example.lifechart.domain.simulation.dto.response.MonthlyAchievement;
import org.example.lifechart.domain.simulation.dto.response.MonthlyAssetDto;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

//이자율로직을 고쳤습니다. 이전 계산에는 마지막 달에는 저축이자 안붙었었음 -> 모든 달에 저축이자 붙도록
@Component
@RequiredArgsConstructor
public class SimulationCalculator {
    //단리  정기적금 마지막 달도 저축 이자가 붙음.
    private static double calculateAccumulatedAssetWithSimpleInterest(
            double monthlySaving,
            double annualInterestRate,
            int monthCount
    ) {
        double interest = monthlySaving
                * (monthCount * (monthCount + 1) / 2.0)
                * (annualInterestRate / 100.0) / 12.0;
        return monthlySaving * monthCount + interest;
    }

    // 1. 앞으로 모아야 하는 금액
    public long calculateRequiredAmount(long initialAsset, List<Goal> selectedGoals) {
        long totalGoalAmount = selectedGoals.stream()
                .mapToLong(Goal::getTargetAmount)
                .sum();
        return Math.max(0, totalGoalAmount - initialAsset);
    }

    // 2. 목표 달성까지 예상 날짜 반환
    //estimatedAchieveMonth": "2027.04"이런 식으로 포맷되게 해놨습니다. 이 전에는 개월 수가 반환됐었음.
    public static YearMonth estimateAchieveMonth(
            double monthlySaving,
            double annualInterestRate,
            double targetAmount,
            double initialAsset,
            YearMonth baseMonth
    ) {
        if (initialAsset >= targetAmount) {
            throw new IllegalArgumentException("목표보다 자산이 이미 많습니다.");
        }

        double savingRatio = monthlySaving / targetAmount;
        if (savingRatio < 0.005) {
            throw new IllegalArgumentException("저축액이 목표 금액의 0.5% 미만입니다. 목표 실현이 어려울 수 있습니다.");
        }

        // a = C * r연 / 2400 -> 소수점 처리 반영
        double a = monthlySaving * (annualInterestRate / 2400.0);

        // b = C * (1 + r연 / 2400)
        double b = monthlySaving * (1 + (annualInterestRate / 2400.0));

        // c = -B
        double c = -(targetAmount - initialAsset);

        // 판별식
        double discriminant = Math.pow(b, 2) - 4 * a * c;

        // D < 0: 판별식이 음수면 실수 해가 없음 -> 이미 달성함.
        if (discriminant < 0) return baseMonth;

        // 근의 공식 부분
        double n = (-b + Math.sqrt(discriminant)) / (2 * a);

        if (n > 600) {
            throw new IllegalArgumentException("50년 이상 소요되는 목표입니다. 저축액을 늘려주세요.");
        }

        int monthsToAchieve = (int) Math.ceil(n);

        return baseMonth.plusMonths(monthsToAchieve);
        //n이 매우커지는 조건은 a,b가 작아지는 경우를 생각해 볼 수 있음.
        //1. 월 저축액 c가 너무 작음
        //2. 목표 금액이 현재 자산보다 매우 큰 경우
        //이것은 저축액c가 너무 낮은 것이라고 볼 수 있기 때문에 savingRatio로 예외처리를 던져 다시 값을 요청받을 수 있도록 로직을 짰습니다. 차라리 c값이 커지도록 해서 n을 작아지게 하는 방향이
        //나을 것이라고 판단한 것 입니다.
        // 제가 생각한 방향은 목표 금액 대비 저축액 비율이 너무 낮은 경우를 이전에 알리는 것이고 기백님이 말씀해주신건(예)예외처리에서 100년 이상 소요되는 목표입니다.)
        //계산 결과가 수행되고 난 이후라 더 강한 예외를 줄 수 있을 것이라 생각됩니다. 여기서 Math.sqrt계산의 성능에 관련해 질문주셔서 고민해보았는데,
        // 이 내장 메서드는 루프 안에서 수천 번 반복되지 않는 이상 성능 이슈는 없다네요.
        // 지피티는 예외처리가 둘 다 존재하면 좋다고 하네요.
    }

    // 3. 현재 달성률 (%) 계산
    // 모든 달의 저축에 이자가 붙는 형식
    public static double calculateProgressRate(
            double initialAsset,
            double monthlySaving,
            int elapsedMonths,
            double annualInterestRate,
            double targetAmount
    ) {
        double accumulatedAmount = calculateAccumulatedAssetWithSimpleInterest(monthlySaving, annualInterestRate, elapsedMonths);
        // 목표 달성률 계산

        double totalAsset = initialAsset + accumulatedAmount;

        double progressRate = (totalAsset / targetAmount) * 100;

        return Math.min(progressRate, 100.0); // 100% 초과 방지
    }


    // 4. 매달 예상 달성률 리스트 반환
    // 매달 말 기준 예상 달성이므로 모든 월의 저축에 이자를 받았다고 간주
    public static List<MonthlyAchievement> calculateMonthlyProgressRates(
            double monthlySaving,
            double annualInterestRate,
            double targetAmount,
            int totalMonths,
            YearMonth baseMonth
    ) {
        List<MonthlyAchievement> progressList = new ArrayList<>();

        for (int monthIndex = 1; monthIndex <= totalMonths; monthIndex++) {
            double accumulated = calculateAccumulatedAssetWithSimpleInterest(
                    monthlySaving, annualInterestRate, monthIndex
            );

            double progressRate = (targetAmount == 0) ? 100.0 : (accumulated / targetAmount) * 100.0;

            if (!Double.isFinite(progressRate)) {
                progressRate = 0.0;
            }

            progressList.add(new MonthlyAchievement(
                    baseMonth.plusMonths(monthIndex - 1),
                    (float) Math.min(progressRate, 100.0)
            ));

            if (progressRate >= 100.0) {
                break;
            }
        }

        return progressList;
    }

    // 5. 매달 자산 변화 시뮬레이션 (자산 금액, 매달 변화)
    public static List<MonthlyAssetDto> simulateMonthlyAssetsWithInterest(
            long initialAsset,
            long monthlySaving,
            double annualInterestRate,
            int totalMonths,
            YearMonth baseMonth
    ) {
        List<MonthlyAssetDto> assets = new ArrayList<>();

        for (int monthIndex = 1; monthIndex <= totalMonths; monthIndex++) {
            double accumulated = calculateAccumulatedAssetWithSimpleInterest(monthlySaving, annualInterestRate, monthIndex);
            long totalAsset = Math.round(initialAsset + accumulated); // 초기 자산 포함

            YearMonth currentMonth = baseMonth.plusMonths(monthIndex - 1);
            assets.add(new MonthlyAssetDto(currentMonth, totalAsset));
        }

        return assets;
    }
}