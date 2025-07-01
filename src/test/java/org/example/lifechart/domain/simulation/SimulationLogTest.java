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
import org.example.lifechart.domain.simulation.dto.request.UpdateSimulationRequestDto;
import org.example.lifechart.domain.simulation.dto.response.SimulationResults;
import org.example.lifechart.domain.simulation.entity.Simulation;
import org.example.lifechart.domain.simulation.entity.SimulationGoal;
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

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@Import(SimulationLogListener.class)
public class SimulationLogTest {

    @Mock
    private SimulationLogServiceImpl simulationLogService;

    @InjectMocks
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
    private SimulationLogEventPublisher simulationLogEventPublisher;

    @InjectMocks
    private SimulationLogListener simulationLogListener;

    @Test
    void 시뮬레이션로그_저장_이벤트_수신_정상_처리() throws JsonProcessingException {
        // given
        Long userId = 1L;
        Long simulationId = 2L;
        Long goalId = 1L;

        var dto = new BaseCreateSimulationRequestDto(
                "5년 뒤 내 집 마련",
                LocalDate.of(2025, 6, 16),
                1_000_000L,
                3_000_000L,
                2_000_000L,
                1_000_000L,
                3.0,
                0,
                60,
                List.of(goalId)
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

        SimulationCreatedEvent event = new SimulationCreatedEvent(
                userId,
                simulationId,
                List.of(goalId),
                paramsJson,
                resultsJson,
                ChangeType.CREATED
        );

        // when
        simulationLogListener.handleSimulationCreated(event);

        // then
        verify(simulationLogService).saveLog(any(SimulationLogSaveDto.class));
    }

    @Test
    void 시뮬레이션로그_저장_이벤트_정상_발행() {

        Long goalId = 1L;
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .nickname("testuser")
                .isDeleted(false)
                .build();

        given(userRepository.findByIdAndDeletedAtIsNull(user.getId()))
                .willReturn(Optional.of(user));

        Goal goal = Goal.builder()
                .id(1L)
                .user(user)
                .title("목표")
                .category(Category.HOUSING)
                .targetAmount(1_000_000L)
                .startAt(LocalDateTime.now())
                .endAt(LocalDateTime.now().plusMonths(6))
                .status(Status.ACTIVE)
                .share(Share.PRIVATE)
                .build();

        given(goalRepository.findAllWithUserByIdAndUserId(List.of(1L), 1L))
                .willReturn(List.of(goal));

        var dto = new BaseCreateSimulationRequestDto(
                "5년 뒤 내 집 마련",
                LocalDate.of(2025, 6, 16),
                1_000_000L,
                3_000_000L,
                2_000_000L,
                1_000_000L,
                3.0,
                0,
                60,
                List.of(goalId)
        );

        SimulationResults mockResults = SimulationResults.builder()
                .requiredAmount(8_000_000L)
                .estimatedAchieveMonth("2025-6")
                .currentAchievementRate(10.0f)
                .monthlyAchievements(List.of())
                .monthlyAssets(List.of())
                .build();

        given(calculateAll.calculate(
                anyLong(), anyLong(), anyLong(), anyLong(), anyDouble(),
                anyInt(), anyInt(), any(LocalDate.class), any(LocalDate.class), anyList()
        )).willReturn(mockResults);

        //서비스 코드 내에서 simulation.getid()가 null이 되니까 실제로 save에 전달된 simulation객체를 꺼내서 id필드 찾기
        //테스트 할때 saveSImulation을 실제로 호출하면 테스트에서 만든 id와 불일치 문제
        given(simulationRepository.save(any(Simulation.class)))
                .willAnswer(invocation -> {
                    Simulation sim = invocation.getArgument(0);
                    // ID를 심어줌 (서비스 내부에서 getId()가 null 방지)
                    Field idField = Simulation.class.getDeclaredField("id");
                    idField.setAccessible(true);
                    idField.set(sim, 2L);
                    return sim;
                });

        simulationService.saveSimulation(dto, user.getId(), List.of(goalId));

        verify(simulationLogEventPublisher).publishCreateEvent(
                eq(1L),
                eq(2L),
                eq(List.of(goalId)),
                eq(dto),
                eq(mockResults)
        );
    }


    @Test
    void 목표수정_시뮬레이션수정_정상발행()  {
        // given
        Long goalId = 1L;
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .nickname("testuser")
                .isDeleted(false)
                .build();

        given(userRepository.findByIdAndDeletedAtIsNull(user.getId()))
                .willReturn(Optional.of(user));

        Goal goal = Goal.builder()
                .id(1L)
                .user(user)
                .title("목표")
                .category(Category.HOUSING)
                .targetAmount(1_000_000L)
                .startAt(LocalDateTime.now())
                .endAt(LocalDateTime.now().plusMonths(6))
                .status(Status.ACTIVE)
                .share(Share.PRIVATE)
                .build();

        given(goalRepository.findByIdAndUserId(goalId, user.getId()))
                .willReturn(Optional.of(goal));

                Simulation simulation = Simulation.builder()
                .id(2L)
                .user(user)
                .title("테스트 시뮬레이션")
                .isDeleted(false)
                .initialAsset(1000000L)
                .monthlyIncome(300000L)
                .monthlyExpense(100000L)
                .monthlySaving(200000L)
                .annualInterestRate(2.5)
                .elapsedMonths(0)
                .totalMonths(60)
                .baseDate(LocalDate.now())
                .build();

        SimulationGoal simulationGoal = SimulationGoal.builder()
                .goal(goal)
                .simulation(simulation)
                .build();

        given(simulationGoalRepository.findAllByGoalIdAndSimulationUserIdAndActiveTrue(user.getId(), goalId))
                .willReturn(List.of(simulationGoal));

        given(simulationGoalRepository.findActiveGoalsBySimulationId(simulation.getId()))
                .willReturn(List.of(goal));

        SimulationResults mockResults = SimulationResults.builder()
                .requiredAmount(8_000_000L)
                .estimatedAchieveMonth("2025-6")
                .currentAchievementRate(10.0f)
                .monthlyAchievements(List.of())
                .monthlyAssets(List.of())
                .build();

        given(calculateAll.calculate(
                anyLong(), anyLong(), anyLong(), anyLong(), anyDouble(),
                anyInt(), anyInt(), any(LocalDate.class), any(LocalDate.class), anyList()
        )).willReturn(mockResults);

        simulationService.updateSimulationsByGoalChange(user.getId(), goalId);

        verify(simulationLogEventPublisher).publishUpdateEventByGoalChange(
                eq(1L),
                eq(2L),
                eq(1L),
                eq(mockResults));
    }

    @Test
    void 목표수정_시뮬레이션수정_이벤트_수신_정상_처리() throws JsonProcessingException{

        Long userId = 1L;
        Long simulationId = 2L;
        Long goalId = 1L;

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

        String resultsJson = objectMapper.writeValueAsString(mockResults);


        SimulationCreatedEvent event = new SimulationCreatedEvent(
                userId,
                simulationId,
                List.of(goalId),
                "{}",
                resultsJson,
                ChangeType.UPDATED_BY_GOAL_CHANGE
        );

        // when
        simulationLogListener.handleSimulationCreated(event);

        // then
        verify(simulationLogService).saveLog(any(SimulationLogSaveDto.class));


    }

    @Test
    void 시뮬레이션수정_시_이벤트_정상_발행() {

        Long goalId = 1L;
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .nickname("testuser")
                .isDeleted(false)
                .build();

        given(userRepository.findByIdAndDeletedAtIsNull(user.getId()))
                .willReturn(Optional.of(user));

        Goal goal = Goal.builder()
                .id(1L)
                .user(user)
                .title("목표")
                .category(Category.HOUSING)
                .targetAmount(1_000_000L)
                .startAt(LocalDateTime.now())
                .endAt(LocalDateTime.now().plusMonths(6))
                .status(Status.ACTIVE)
                .share(Share.PRIVATE)
                .build();

        given(goalRepository.findAllWithUserByIdAndUserId(List.of(1L), 1L))
                .willReturn(List.of(goal));

        Simulation simulation = Simulation.builder()
                .id(2L)
                .user(user)
                .title("테스트 시뮬레이션")
                .isDeleted(false)
                .initialAsset(1000000L)
                .monthlyIncome(300000L)
                .monthlyExpense(100000L)
                .monthlySaving(200000L)
                .annualInterestRate(2.5)
                .elapsedMonths(0)
                .totalMonths(60)
                .baseDate(LocalDate.now())
                .build();

        given(simulationRepository.findById(2L)).willReturn(Optional.of(simulation));

        UpdateSimulationRequestDto dto = new UpdateSimulationRequestDto(
                "5년 뒤 내 집 마련",
                LocalDate.of(2025, 6, 16),
                1_000_000L,
                3_000_000L,
                2_000_000L,
                1_000_000L,
                3.0,
                0,
                60,
                List.of(goalId)
        );

        SimulationResults mockResults = SimulationResults.builder()
                .requiredAmount(8_000_000L)
                .estimatedAchieveMonth("2025-6")
                .currentAchievementRate(10.0f)
                .monthlyAchievements(List.of())
                .monthlyAssets(List.of())
                .build();

        given(calculateAll.calculate(
                anyLong(), anyLong(), anyLong(), anyLong(), anyDouble(),
                anyInt(), anyInt(), any(LocalDate.class), any(LocalDate.class), anyList()
        )).willReturn(mockResults);

        simulationService.updateSimulationSettings(user.getId(), simulation.getId(), List.of(goalId), dto);

        verify(simulationLogEventPublisher).publishUpdateEventBySimulationEdit(
                eq(1L),
                eq(2L),
                eq(List.of(1L)),
                any(UpdateSimulationRequestDto.class),
                any(SimulationResults.class)
        );

    }

    @Test
    void 시뮬레이션수정_이벤트_수신_정상_처리() throws JsonProcessingException{
        // given
        Long userId = 1L;
        Long simulationId = 2L;
        List<Long> goalIds = List.of(1L);

        UpdateSimulationRequestDto dto = new UpdateSimulationRequestDto(
                "5년 뒤 내 집 마련",
                LocalDate.of(2025, 6, 16),
                1_000_000L,
                3_000_000L,
                2_000_000L,
                1_000_000L,
                3.0,
                0,
                60,
                goalIds
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

        SimulationCreatedEvent event = new SimulationCreatedEvent(
                userId,
                simulationId,
                goalIds,
                paramsJson,
                resultsJson,
                ChangeType.UPDATED_BY_SIMULATION_EDIT
        );

        // when
        simulationLogListener.handleSimulationCreated(event);

        // then
        verify(simulationLogService).saveLog(any(SimulationLogSaveDto.class));
    }

}

