package org.example.lifechart.domain.simulation.service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.example.lifechart.domain.simulation.dto.response.SimulationResponseDto;
import org.example.lifechart.domain.simulation.dto.response.SimulationSummaryDto;
import org.example.lifechart.domain.simulation.entity.Simulation;
import org.example.lifechart.domain.simulation.repository.SimulationRepository;
import org.example.lifechart.domain.user.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SimulationServiceImpl implements SimulationService {

    private final SimulationRepository simulationRepository;

    @Transactional
    public SimulationResponseDto saveSimulation(SimulationResponseDto dto, User user) {

        //존재하지 않는 goalId가 포함

        //빈 목표 리스트 요청 valid처리

        //수식 -> monthlyIncome, monthlyExpense 음수 여부, 최소값, initialAsset null여부

        //빌더 패턴
        Simulation simulation = Simulation.builder()
                    .title(dto.getTitle())
                    .baseDate(dto.getBaseDate())
                    .initialAsset(dto.getInitialAsset())
                    .monthlyIncome(dto.getMonthlyIncome())
                    .monthlyExpense(dto.getMonthlyExpense())
                    .user(user)
                    .params(dto.getParams())
                    .results(dto.getResults()) // 최초 생성
                    .build();

        //계산 수행
        //result = .. Calculator.calculate();
        //빌더 패턴에 같이 넣으면 계산이 같이 가능할까???
        //사용자는 여러 목표를 선택할 수 있으며, 선택된 목표 유형에 따른 계산로직이 실행된다.

        simulationRepository.save(simulation);
        return SimulationResponseDto.toDto(simulation);
    }

    //모든 정보가 아니라 어떤 목록이 있는지 id와 title만 ..
    //readonly
    public List<SimulationSummaryDto> findAllSimulationsByUserId(Long userId) {

        User user = userService.findByOrElseThrow(userId);

        return simulationRepository.findAllByUser(user)
                .stream()
                .map(SimulationSummaryDto::toDto)
                .collect(Collectors.toList());
    }

    //id에 해당하는 모든 항목들을 조회
    public SimulationResponseDto findSimulationById(Long simulationId) {

        //커스텀예외처리 필요
        Simulation simulation = simulationRepository.findById(simulationId)
                .orElseThrow(() -> new RuntimeException("Simulation with id " + simulationId + " not found"));

        return SimulationResponseDto.toDto(simulation);

    }
    @Transactional
    public SimulationResponseDto updateSimulation(SimulationResponseDto dto, User user) {
            //goal이 목표 수정했는지  -> 목표가 수정되면 목표 ID를 확보함.
            // 수정된 목표가 연결된 시뮬레이션 조회
            // 각 시뮬레이션에 대해 재계산 로직

    }

    @Transactional
    public void deleteSimulationById(Long simulationId) {
        Simulation simulation = simulationRepository.findById(simulationId)
                .orElseThrow(() -> new RuntimeException("notfound"));

        simulationRepository.delete(simulation);
    }

    //readonly주의
}


