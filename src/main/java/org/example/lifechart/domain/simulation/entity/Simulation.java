package org.example.lifechart.domain.simulation.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.lifechart.common.entity.BaseEntity;
import org.example.lifechart.domain.simulation.dto.request.BaseCreateSimulationRequestDto;
import org.example.lifechart.domain.simulation.dto.response.MonthlyAchievement;
import org.example.lifechart.domain.simulation.dto.response.MonthlyAssetDto;
import org.example.lifechart.domain.simulation.dto.response.SimulationResults;
import org.example.lifechart.domain.user.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
@Builder
@Table(name = "simulation")
public class Simulation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String title;

    //기준일 사용자 설정 가능
    @Column(nullable = false)
    private LocalDate baseDate;

    //최초 자산
    @Column(nullable = false)
    private Long initialAsset;

    //수입
    @Column(nullable = false)
    private Long monthlyIncome;

    //지출
    @Column(nullable = false)
    private Long monthlyExpense;

    //simulation이 save될 때 연결된 SimulationGoal도 자동으로 save됨.
    @Builder.Default
    @OneToMany(mappedBy = "simulation", cascade = CascadeType.ALL, orphanRemoval = true )
    private List<SimulationGoal> simulationGoals = new ArrayList<>();

    //JPA가 해당 필드를 DB에 저장하거나 읽을 때 사용할 변환 로직(추후 수정예정)
    //파람은 복합객체라 이것을 직렬화, 역직렬화 해야 함.
//    @Convert(converter = SimulationParamsConverter.class)
//    @Column(columnDefinition = "json")
//    private SimulationParams params;
//
//    @Convert(converter = SimulationResultsConverter.class)
//    @Column(columnDefinition = "json")
//    private SimulationResults results;

    @Builder.Default
    @Column(nullable = true)
    private boolean isDeleted = false;

    @Column(nullable = true)
    private LocalDateTime deletedAt;

    //사용자 입력 연이율
    @Column(nullable = false)
    private double annualInterestRate;

    //기준일로부터 경과함 개월수(누적자산계산에 쓰임)
    @Column(nullable = false)
    private int elapsedMonths;

    //앞으로 더 모아야하는 금액
    @Column(nullable = false)
    private Long requiredAmount;

    //목표까지 남은 예상 개월 수
    @Column(nullable = false)
    private Integer monthsToGoal;

    //현재 달성률
    @Column(nullable = false)
    private Float currentAchievementRate;

    //전체 시뮬레이션 기간 -> 시뮬레이션 대상 기간동안 월별 변화 추이
    @Column(nullable = false)
    private int totalMonths;

    //매달 저축되는 금액(수입-지출)
    @Column(nullable = false)
    private Long monthlySaving;

    //시뮬레이션 테이블에 list로 저장하고싶으면 필요한 어노테이션.
    @ElementCollection
    private List<MonthlyAchievement> monthlyAchievements;

    @ElementCollection
    private List<MonthlyAssetDto> monthlyAssets;

    //소프트릴리트 DB저장 필드 값 변경
    public void softDelete() {
        if (!this.isDeleted) {
            this.isDeleted = true;
            this.deletedAt = LocalDateTime.now();
        }
    }

    // 기존 시뮬레이션 연결 끊고 전체 새로 연결. 업데이트할 때 필요함.
    public void addSimulationGoals(List<SimulationGoal> simulationGoals) {
        // 기존 SimulationGoal 들과 연결 끊기
        for (SimulationGoal simulationGoal : this.simulationGoals) {
            simulationGoal.setSimulation(null);
        }
        this.simulationGoals.clear();

        // 새로운 SimulationGoal 들 추가 (양방향 동기화 포함)
        for (SimulationGoal simulationGoal : simulationGoals) {
            this.addSimulationGoal(simulationGoal);
        }
    }

    public void addSimulationGoal(SimulationGoal simulationGoal) {
        // 중복 방지
        if (!this.simulationGoals.contains(simulationGoal)) {
            this.simulationGoals.add(simulationGoal);
            simulationGoal.setSimulation(this); // 양방향 연결
        }
    }

    public void addSimulationGoalList(List<SimulationGoal> simulationGoals) {
        for (SimulationGoal simulationGoal : simulationGoals) {
            this.addSimulationGoal(simulationGoal);
        }
    }

    public static Simulation createSimulation(BaseCreateSimulationRequestDto dto, SimulationResults results, User user) {
        return Simulation.builder()
                .title(dto.getTitle())
                .baseDate(dto.getBaseDate())
                .initialAsset(dto.getInitialAsset())
                .monthlyIncome(dto.getMonthlyIncome())
                .monthlyExpense(dto.getMonthlyExpense())
                .monthlySaving(dto.getMonthlySaving())
                .annualInterestRate(dto.getAnnualInterestRate())
                .elapsedMonths(dto.getElapsedMonths())
                .totalMonths(dto.getTotalMonths())
                .requiredAmount(results.getRequiredAmount())
                .monthsToGoal(results.getMonthsToGoal())
                .currentAchievementRate(results.getCurrentAchievementRate())
                .monthlyAchievements(results.getMonthlyAchievements())
                .monthlyAssets(results.getMonthlyAssets())
                .user(user)
                .build();
    }


}
