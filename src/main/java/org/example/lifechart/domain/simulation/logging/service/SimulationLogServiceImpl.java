package org.example.lifechart.domain.simulation.logging.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.lifechart.domain.simulation.logging.dto.SimulationLogSaveDto;
import org.example.lifechart.domain.simulation.logging.entity.SimulationLog;
import org.example.lifechart.domain.simulation.logging.repository.SimulationLogRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SimulationLogServiceImpl implements SimulationLogService{

    private final SimulationLogRepository simulationLogRepository;
    private final ObjectMapper objectMapper;

    //로그를 저장하기 위한 로직
    @Override
    @Transactional
    public void saveLog(SimulationLogSaveDto dto) throws JsonProcessingException {

        //goalList를 문자열로 저장할거임
        String goalIdsJson = objectMapper.writeValueAsString(dto.getGoalIds());

        SimulationLog log = SimulationLog.builder()
                .userId(dto.getUserId())
                .simulationId(dto.getSimulationId())
                .goalIds(goalIdsJson)
                .params(dto.getParams())
                .results(dto.getResults())
                .changeType(dto.getChangeType())
                .build();

        simulationLogRepository.save(log);
    }

}