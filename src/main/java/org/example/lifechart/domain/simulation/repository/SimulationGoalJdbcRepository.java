package org.example.lifechart.domain.simulation.repository;

import lombok.RequiredArgsConstructor;
import org.example.lifechart.domain.simulation.entity.SimulationGoal;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SimulationGoalJdbcRepository {

    private final JdbcTemplate jdbcTemplate;
    //goalDto리스트를 받아 batch insert실행
    public void batchInsertSimulationGoals(List<SimulationGoal> simulationGoals) {
        String sql = """
            INSERT INTO simulation_goal (
                simulation_id,
                goal_id,
                active,
                linked_at
            )
            VALUES (?, ?, ?, ?)
        """;
        //배치 업데이트하려면 list<[object]>파라미터 리스트 필요.
        //이 row가 preparedStatement준비 후 한번에 insert처리됨.
        //goal.getSimulation() → 이 SimulationGoal이 어떤 Simulation과 연결되어 있는지 가져옴.
        List<Object[]> batchArgs = simulationGoals.stream()
                .map(goal -> new Object[]{
                        goal.getSimulation().getId(),
                        goal.getGoal().getId(),
                        goal.isActive(),
                        goal.getLinkedAt()
                })
                .toList();

        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    //이건 시뮬레이션 업데이트시 연결끊길 때.
    public void deactivateSimulationGoals(Long simulationId) {
        //AND is_active = true 는 is_active가 true인 row만 업데이트 현재 연결중인 row만 끊음.
        String sql = """
              UPDATE simulation_goal SET
              active = false
              unlinked_at = NOW()
              WHERE simulation_id = ?
              AND active = true
              """;

        jdbcTemplate.update(sql, simulationId);

    }
}

