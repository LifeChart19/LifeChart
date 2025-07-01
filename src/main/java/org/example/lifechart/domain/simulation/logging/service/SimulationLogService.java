package org.example.lifechart.domain.simulation.logging.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.lifechart.domain.simulation.logging.dto.SimulationLogSaveDto;

public interface SimulationLogService {

    void saveLog(SimulationLogSaveDto dto) throws JsonProcessingException;

}
