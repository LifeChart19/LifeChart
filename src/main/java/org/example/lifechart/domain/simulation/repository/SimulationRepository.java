package org.example.lifechart.domain.simulation.repository;

import org.example.lifechart.domain.simulation.entity.Simulation;
import org.example.lifechart.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SimulationRepository extends JpaRepository<Simulation, Integer> {

    Optional<Simulation> findById(Long simulationId);

    List<Simulation> findAllByUser(User user);
}
