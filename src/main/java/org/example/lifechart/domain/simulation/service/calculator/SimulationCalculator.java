package org.example.lifechart.domain.simulation.service.calculator;

import lombok.extern.log4j.Log4j2;
import org.example.lifechart.domain.goal.entity.Goal;
import org.example.lifechart.domain.simulation.dto.response.MonthlyAchievement;
import org.example.lifechart.domain.simulation.dto.response.MonthlyAssetDto;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

//이자율로직을 고쳤습니다. 이전 계산에는 마지막 달에는 저축이자 안붙었었음 -> 모든 달에 저축이자 붙도록
@Component
@Log4j2
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
            return baseMonth;
        }

        //double savingRatio = monthlySaving / targetAmount;
        //        if (savingRatio < 0.0014) { //0.5가 아니라 0.05였어야 했음. ->대충 50년 정도됨.
        //            //이 부분은 경고메시지로 바꿀 예정 응답에 경고 메시지 포함으로 추후 반영
        //            System.out.println("계산은 완료되었지만 저축액이 너무 적어 목표달성까지 장기간 소요됨");


        // a = C * r연 / 2400 -> 소수점 처리 반영
        double a = monthlySaving * (annualInterestRate / 2400.0);

        // b = C * (1 - r연 / 2400)
        double b = monthlySaving * (1 - (annualInterestRate / 2400.0));

        // c = -B
        double c = -(targetAmount - initialAsset);

        //이미 자산이 목표보다 많거나 같으면 달성 월은 이 시점.

        // 판별식
        double discriminant = Math.pow(b, 2) - 4 * a * c;

        // D < 0: 판별식이 음수면 실수 해가 없음 -> 이미 달성함.
        if (discriminant < 0) return baseMonth;

        // 근의 공식 부분
        double n = (-b + Math.sqrt(discriminant)) / (2 * a);

        //경고메시지를 만드려고 했는데, 테스트가 잘 안돼서 하지 못했습니다. 오늘 PR검토하면서 계속 알아봐볼게요
    //    if (n > 600) {
    //        throw new IllegalArgumentException("50년 이상 소요되는 목표입니다. 저축액을 늘려주세요.");//   }


        int monthsToAchieve = (int) Math.ceil(n);

        return baseMonth.plusMonths(monthsToAchieve);
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
    // 기대수명에 따른 자산변화로 변경
    public static List<MonthlyAssetDto> simulateMonthlyAssetsWithInterest(
            long initialAsset,
            long monthlySaving,
            double annualInterestRate,
            LocalDate baseDate,
            LocalDate expectedDeathDate
    ) {

        List<MonthlyAssetDto> monthlyAssets = new ArrayList<>();
        // 기준이 되는 날짜
        YearMonth currentMonth = YearMonth.from(baseDate);
        // 기대수명일을 월 단위로 변환 2025-5
        YearMonth endMonth = YearMonth.from(expectedDeathDate);
        // 월 이율로 변환
        double monthlyRate = annualInterestRate / 12.0 / 100.0;
        // 총 시뮬레이션 기간 월을 계산 예를 들어 2025년 6월 부터 2085년 6월까지는 721개월
        int totalMonths = (int) ChronoUnit.MONTHS.between(currentMonth, endMonth) + 1;

        // 721개월 totalMonths만큼 for문을 반복
        for (int month = 1; month <= totalMonths; month++) {
            YearMonth targetMonth = currentMonth.plusMonths(month - 1);

            // 초기 자산 이자 -> 초기 자산에 대한 누적 이자 계산 이자 = 원금 x이율x기간
            double initialAssetInterest = initialAsset * monthlyRate * month;

            // 축 누적 자산 (단리) → 기존 함수 사용
            double savingWithInterest = calculateAccumulatedAssetWithSimpleInterest(
                    monthlySaving, annualInterestRate, month
            );

            // 총 자산 -> 초기 자산 + 초기자산 단리 리이자+저축원금에 이자
            double total = initialAsset + initialAssetInterest + savingWithInterest;

            monthlyAssets.add(new MonthlyAssetDto(targetMonth, Math.round(total)));
        }
        //total = monthlySaving * months + monthlySaving * monthlyRate * (months * (months + 1)) / 2;
        return monthlyAssets;
    }
}

