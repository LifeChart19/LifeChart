package org.example.lifechart.domain.simulation.service;

//readonly주의할 것.

import org.example.lifechart.domain.simulation.dto.response.SimulationResponseDto;
import org.example.lifechart.domain.simulation.dto.response.SimulationSummaryDto;
import org.example.lifechart.domain.user.entity.User;

import java.util.List;

public interface SimulationService {
    SimulationResponseDto saveSimulation(SimulationResponseDto dto, User user);
    List<SimulationSummaryDto> findAllSimulationsByUserId(Long userId);
    SimulationResponseDto findSimulationById(Long simulationId);
    SimulationResponseDto updateSimulation(SimulationResponseDto dto, User user);
    void deleteSimulationById(Long simulationId);


  //  SimulationResponseDto updateSimulation(SimulationResponseDto simulationResponseDto);

}
