package org.example.lifechart.domain.simulation.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.lifechart.common.entity.BaseEntity;
import org.example.lifechart.domain.simulation.converter.SimulationParamsConverter;
import org.example.lifechart.domain.user.entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
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

    private String title;

    //기준일 사용자 설정 가능
    private LocalDateTime baseDate;

    //최초 자산
    private Long initialAsset;

    //수입
    private Long monthlyIncome;

    //지출
    private Long monthlyExpense;

    @OneToMany(mappedBy = "simulation", cascade = CascadeType.ALL, orphanRemoval = true )
    private List<SimulationGoal> simulationGoals = new ArrayList<>();
    //JPA가 해당 필드를 DB에 저장하거나 읽을 때 사용할 변환 로직
    //파람은 복합객체라 이것을 직렬화, 역직렬화 해야 함.
    @Convert(converter = SimulationParamsConverter.class)
    @Column(columnDefinition = "json")
    private SimulationParams params;

    @Convert(converter = SimulationParamsConverter.class)
    @Column(columnDefinition = "json")
    private SimulationParams results;



}
