package org.example.lifechart.domain.simulation.service.simulation;

//readonly주의할 것.

import org.example.lifechart.domain.simulation.dto.request.BaseCreateSimulationRequestDto;
import org.example.lifechart.domain.simulation.dto.response.CreateSimulationResponseDto;
import org.example.lifechart.domain.simulation.dto.response.DeletedSimulationResponseDto;
import org.example.lifechart.domain.simulation.dto.response.SimulationSummaryDto;

import java.util.List;

public interface SimulationService {
     CreateSimulationResponseDto saveSimulation(BaseCreateSimulationRequestDto dto, Long userId, List<Long> goalIds);
     List<SimulationSummaryDto> findAllSimulationsByUserId(Long userId);
//    BaseSimulationResponseDto findSimulationById(User user, Long simulationId);
//    List<DeletedSimulationResponseDto> findAllSoftDeletedSimulations(Long userId);
      DeletedSimulationResponseDto softDeleteSimulation(Long userId, Long simulationId);
      void deleteSimulation(Long userId, Long simulationId);
//    SimulationResponseDto updateSimulation(SimulationResponseDto simulationResponseDto);

}
