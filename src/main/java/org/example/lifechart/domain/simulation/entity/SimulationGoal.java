package org.example.lifechart.domain.simulation.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.lifechart.domain.goal.entity.Goal;

import java.time.LocalDateTime;

@Builder
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SimulationGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long simulationGoalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "simulation_id")
    @Setter(AccessLevel.PROTECTED)
    private Simulation simulation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id")
    private Goal goal;

    private LocalDateTime linkedAt;

    private LocalDateTime unlinkedAt;

    private boolean isActive;
    //boolean 타입의 필드명을 isActive로 만들어놓으면 JPA나 Lombok에서 getter 이름이 isIsActive()라고 생성됨
    //그래서 active로 설정하는 것이 좋을듯 현재 이 필드가 없으면 오류남.
    private boolean active;

//    //시뮬레이션 필드를 설정하여 시뮬레이션골이 어디에 속한 시뮬레이션인지 ..
//    public void setSimulation(Simulation simulation) {
//        this.simulation = simulation;
//        // 역으로 Simulation.simulationGoals 에 추가하는 로직은 넣지 않음 → 무한 루프 방지
//        //시뮬레이션에서 시뮬레이션 골을 가져와 추가하는 로직을 하면 무한루프 발생하는데,
//        //관계 정합성을 편의메서드에서 다 관리함.
//    }


    public void updateWithGoal(Goal goal) {
        this.goal = goal;
        this.linkedAt = LocalDateTime.now();
        this.isActive = true;

    }

    public void inactive() {
        this.isActive = false;
        this.unlinkedAt = LocalDateTime.now();
    }

}
