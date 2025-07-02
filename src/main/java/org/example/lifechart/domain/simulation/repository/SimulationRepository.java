package org.example.lifechart.domain.simulation.repository;

import org.example.lifechart.domain.simulation.entity.Simulation;
import org.example.lifechart.domain.user.entity.User;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SimulationRepository extends JpaRepository<Simulation, Long> {

    Optional<Simulation> findById(Long simulationId);

    List<Simulation> findAllByUser(User user);

    @Query("SELECT s FROM Simulation s WHERE s.user.id = :userId AND s.isDeleted = true")
    List<Simulation> findAllByUserIdAndIsDeletedTrue(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE Simulation s SET s.deletedAt = CURRENT_TIMESTAMP WHERE s.id = :simulationId")
    void softDeleteById(@Param("simulationId") Long simulationId);
}
