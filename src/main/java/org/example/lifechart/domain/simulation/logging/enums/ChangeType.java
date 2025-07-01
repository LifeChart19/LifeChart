package org.example.lifechart.domain.simulation.logging.enums;

public enum ChangeType {
    CREATED,                     // 시뮬레이션 최초 생성
    UPDATED_BY_SIMULATION_EDIT, // 사용자가 시뮬레이션을 직접 수정한 경우
    UPDATED_BY_GOAL_CHANGE      // 목표가 수정되어 시뮬레이션이 간접 수정된 경우
}
