package org.example.lifechart.domain.simulation.logging.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.example.lifechart.common.entity.BaseEntity;
import org.example.lifechart.domain.simulation.logging.enums.ChangeType;

import java.time.LocalDateTime;

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

    /**
     *  JPA에서는 컬렉션(List<Long> 등)을 직접 DB에 매핑하기 어려워서
     *  JSON 문자열(String)로 변환하여 저장합니다.
     *  직렬화/역직렬화는 ObjectMapper 등을 통해 처리합니다.
     */
    @Lob
    @Column(name = "goal_ids", columnDefinition = "TEXT", nullable = false)
    private String goalIds;

    @Lob
    @Column(nullable = false)
    private String params;

    @Lob
    @Column(name = "results", columnDefinition = "TEXT", nullable = false)
    private String results;

    @Enumerated(EnumType.STRING)
    @Column(name = "change_type", nullable = false)
    private ChangeType changeType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;


}