package org.example.lifechart.domain.simulation.logging.repository;

import org.example.lifechart.domain.simulation.logging.entity.SimulationLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SimulationLogRepository extends JpaRepository<SimulationLog, Long> {
}
