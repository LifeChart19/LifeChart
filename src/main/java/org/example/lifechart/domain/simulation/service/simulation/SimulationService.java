package org.example.lifechart.domain.simulation.service.simulation;

//readonly주의할 것.

import org.example.lifechart.domain.simulation.dto.request.BaseCreateSimulationRequestDto;
import org.example.lifechart.domain.simulation.dto.response.BaseSimulationResponseDto;
import org.example.lifechart.domain.simulation.dto.response.SimulationSummaryDto;
import org.example.lifechart.domain.user.entity.User;

import java.util.List;

public interface SimulationService {
    BaseSimulationResponseDto saveSimulation(BaseCreateSimulationRequestDto dto, User user, List<Long> goalIds) ;
    List<SimulationSummaryDto> findAllSimulationsByUserId(Long userId);
    BaseSimulationResponseDto findSimulationById(Long simulationId);
    BaseSimulationResponseDto updateSimulation(BaseSimulationResponseDto dto, User user);
    void deleteSimulationById(Long simulationId);


  //  SimulationResponseDto updateSimulation(SimulationResponseDto simulationResponseDto);

}
