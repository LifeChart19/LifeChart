package org.example.lifechart.domain.simulation.logging.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.lifechart.common.entity.BaseEntity;
import org.example.lifechart.domain.simulation.logging.enums.ChangeType;

@Getter
@Builder
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "simulation_log")
public class SimulationLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long simulationId;

    //길이가 긴 데이터를 DB에 저장할 때 ->Lob 문자열 길이 제한을 넘길 수 있음.
    @Lob
    @Column(nullable = false)
    private String goalIds;

    @Lob
    @Column(nullable = false)
    private String params;

    @Lob
    @Column(nullable = false)
    private String results;

    @Enumerated(EnumType.STRING)
    @Column(name = "change_type", nullable = false)
    private ChangeType changeType;


}