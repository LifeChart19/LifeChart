package org.example.lifechart.domain.simulation;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.lifechart.domain.goal.entity.Goal;
import org.example.lifechart.domain.goal.enums.Category;
import org.example.lifechart.domain.goal.enums.Share;
import org.example.lifechart.domain.goal.enums.Status;
import org.example.lifechart.domain.goal.repository.GoalRepository;
import org.example.lifechart.domain.simulation.dto.request.BaseCreateSimulationRequestDto;
import org.example.lifechart.domain.simulation.dto.response.SimulationResults;
import org.example.lifechart.domain.simulation.entity.Simulation;
import org.example.lifechart.domain.simulation.event.SimulationCreatedEvent;
import org.example.lifechart.domain.simulation.listener.SimulationLogListener;
import org.example.lifechart.domain.simulation.logging.dto.SimulationLogSaveDto;
import org.example.lifechart.domain.simulation.logging.enums.ChangeType;
import org.example.lifechart.domain.simulation.logging.repository.SimulationLogRepository;
import org.example.lifechart.domain.simulation.logging.service.SimulationLogEventPublisher;
import org.example.lifechart.domain.simulation.logging.service.SimulationLogServiceImpl;
import org.example.lifechart.domain.simulation.repository.SimulationGoalJdbcRepository;
import org.example.lifechart.domain.simulation.repository.SimulationGoalRepository;
import org.example.lifechart.domain.simulation.repository.SimulationRepository;
import org.example.lifechart.domain.simulation.service.calculator.CalculateAll;
import org.example.lifechart.domain.simulation.service.simulation.SimulationServiceImpl;
import org.example.lifechart.domain.user.entity.User;
import org.example.lifechart.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@Import(SimulationLogListener.class)
public class SimulationLogTest {

    @Mock
    private SimulationLogServiceImpl simulationLogService;

    @Mock
    private SimulationServiceImpl simulationService;

    @Mock
    private SimulationRepository simulationRepository;

    @Mock
    private SimulationLogRepository simulationLogRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SimulationGoalRepository simulationGoalRepository;

    @Mock
    private SimulationGoalJdbcRepository simulationGoalJdbcRepository;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private CalculateAll calculateAll;

    @Mock
    private SimulationLogEventPublisher eventPublisher;

    @InjectMocks
    private SimulationLogListener simulationLogListener;

    @Test
    void 시뮬레이션_로그가_저장로직에서_정상적으로_호출() throws JsonProcessingException {
        User user2 = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("password")
                .nickname("testuser")
                .isDeleted(false)
                .build();

        //given(userRepository.findByIdAndDeletedAtIsNull(user2.getId())).willReturn(Optional.of(user2));

        Long goalId = 1L;
        Goal mockGoal = Goal.builder()
                .id(goalId)
                .user(user2) // 꼭 넣어야 함 (nullable = false)
                .title("테스트 목표")
                .category(Category.HOUSING)
                .targetAmount(1_000_000L)
                .startAt(LocalDateTime.now())
                .endAt(LocalDateTime.now().plusMonths(6))
                .status(Status.ACTIVE)
                .share(Share.PRIVATE)
                .build();

        //given(goalRepository.findAllWithUserByIdAndUserId(List.of(goalId), user2.getId()))
        //.willReturn(List.of(mockGoal));

        //simulationParam(json에 필요한 엔티티)
        LocalDate baseDate = LocalDate.of(2025, 6, 16);

        var dto = new BaseCreateSimulationRequestDto(
                "5년 뒤 내 집 마련",
                baseDate,
                1_000_000L,
                3_000_000L,
                2_000_000L,
                1_000_000L,
                3.0,
                0,
                60,
                List.of(mockGoal.getId())
        );

        SimulationResults mockResults = SimulationResults.builder()
                .requiredAmount(8_000_000L)
                .estimatedAchieveMonth("2025-6")
                .currentAchievementRate(10.0f)
                .monthlyAchievements(List.of())
                .monthlyAssets(List.of())
                .build();

        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        String paramsJson = objectMapper.writeValueAsString(dto);
        String resultsJson = objectMapper.writeValueAsString(mockResults);

//        given(calculateAll.calculate(
//                anyLong(), anyLong(), anyLong(), anyLong(), anyDouble(),
//                anyInt(), anyInt(), any(LocalDate.class), any(LocalDate.class), anyList()
//        )).willReturn(mockResults);

        Simulation simulation = Simulation.builder()
                .id(2L)
                .user(user2)
                .title("simulation")
                .isDeleted(true) // 이미 soft delete 되어야 함
                .build();

        //given(simulationRepository.save(any(Simulation.class))).willReturn(simulation);

        // when
        simulationService.saveSimulation(dto, user2.getId(), List.of(mockGoal.getId()));

        //이벤트 발행
        SimulationCreatedEvent event = new SimulationCreatedEvent(
                user2.getId(),
                simulation.getId(),
                List.of(goalId),
                paramsJson,
                resultsJson,
                ChangeType.CREATED
        );

        //로그 저장 서비스 호출
        simulationLogListener.handleSimulationCreated(event);

        // then
        verify(simulationLogService).saveLog(any(SimulationLogSaveDto.class));
    }




    //목표 업데이트 -> 시뮬레이션 업데이트
    @Test
    void 시뮬레이션_로그가_업데이트에서_정상적으로_호출() throws JsonProcessingException {

        User user2 = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("password")
                .nickname("testuser")
                .isDeleted(false)
                .build();

        //given(userRepository.findByIdAndDeletedAtIsNull(user2.getId())).willReturn(Optional.of(user2));
        Long goalId = 1L;

        //given(goalRepository.findAllWithUserByIdAndUserId(List.of(goalId), user2.getId()))
        //.willReturn(List.of(mockGoal));

        //simulationParam(json에 필요한 엔티티)
        LocalDate baseDate = LocalDate.of(2025, 6, 16);

        Simulation simulation = Simulation.builder()
                .id(2L)
                .user(user2)
                .title("simulation")
                .isDeleted(true) // 이미 soft delete 되어야 함
                .build();

        simulationService.updateSimulationsByGoalChange(user2.getId(), simulation.getId());

        String paramsJson = "{\"param\": \"value\"}";
        String resultsJson = "{\"result\": \"value\"}";

        SimulationCreatedEvent event = new SimulationCreatedEvent(
                user2.getId(),
                simulation.getId(),
                List.of(goalId),
                paramsJson,
                resultsJson,
                ChangeType.CREATED
        );

        //로그 저장 서비스 호출
        simulationLogListener.handleSimulationCreated(event);

        verify(simulationLogService).saveLog(any(SimulationLogSaveDto.class));
    }

    @Test
    void 리스너가_이벤트를_잘_처리하는지_확인() throws JsonProcessingException {
        // given
        User user2 = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("password")
                .nickname("testuser")
                .isDeleted(false)
                .build();

                Goal mockGoal = Goal.builder()
                .id(1L)
                .user(user2) // 꼭 넣어야 함 (nullable = false)
                .title("테스트 목표")
                .category(Category.HOUSING)
                .targetAmount(1_000_000L)
                .startAt(LocalDateTime.now())
                .endAt(LocalDateTime.now().plusMonths(6))
                .status(Status.ACTIVE)
                .share(Share.PRIVATE)
                .build();

        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        String dummyParamsJson = objectMapper.writeValueAsString("업데이트된 파라미터");
        String dummyResultsJson = objectMapper.writeValueAsString("업데이트된 결과");

        Long simulationId = 2L;

        SimulationCreatedEvent event = new SimulationCreatedEvent(
                user2.getId(),
                simulationId,
                List.of(mockGoal.getId()),
                dummyParamsJson,
                dummyResultsJson,
                ChangeType.UPDATED_BY_GOAL_CHANGE
        );

        // when
        simulationLogListener.handleSimulationCreated(event);

        // then
        verify(simulationLogService).saveLog(any(SimulationLogSaveDto.class));
    }


}

