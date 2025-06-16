package org.example.lifechart.simulation;

import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.goal.entity.Goal;
import org.example.lifechart.domain.goal.enums.Category;
import org.example.lifechart.domain.goal.enums.Share;
import org.example.lifechart.domain.goal.enums.Status;
import org.example.lifechart.domain.goal.repository.GoalRepository;
import org.example.lifechart.domain.simulation.dto.request.BaseCreateSimulationRequestDto;
import org.example.lifechart.domain.simulation.dto.response.BaseSimulationResponseDto;
import org.example.lifechart.domain.simulation.dto.response.DeletedSimulationResponseDto;
import org.example.lifechart.domain.simulation.dto.response.SimulationSummaryDto;
import org.example.lifechart.domain.simulation.entity.Simulation;
import org.example.lifechart.domain.simulation.entity.SimulationResults;
import org.example.lifechart.domain.simulation.repository.SimulationGoalJdbcRepository;
import org.example.lifechart.domain.simulation.repository.SimulationGoalRepository;
import org.example.lifechart.domain.simulation.repository.SimulationRepository;
import org.example.lifechart.domain.simulation.service.calculator.CalculateAll;
import org.example.lifechart.domain.simulation.service.calculator.SimulationCalculator;
import org.example.lifechart.domain.simulation.service.simulation.SimulationServiceImpl;
import org.example.lifechart.domain.simulation.service.simulation.SimulationValidator;
import org.example.lifechart.domain.user.entity.User;
import org.example.lifechart.domain.user.repository.UserRepository;
import org.example.lifechart.domain.user.service.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class SimulationServiceImplTest {


    @InjectMocks
    private SimulationServiceImpl simulationService;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private SimulationRepository simulationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SimulationGoalRepository simulationGoalRepository;

    @Mock
    private SimulationValidator simulationValidator;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private SimulationGoalJdbcRepository simulationGoalJdbcRepository;

    @Mock
    private CalculateAll calculateAll;

    @Mock
    private SimulationCalculator calculator;

    @Test
    @DisplayName("사용자 id로 Simulation 전체 목록 조회 성공")
    void 사용자_id로_Simulation_전체_목록_조회_성공() {
        //given
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("password")
                .nickname("testuser")
                .build();

        // UserRepository id찾으면 user를 줌.
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

        // Simulation 데이터 준비
        Simulation simulation = Simulation.builder()
                .id(1L)
                .user(user)
                .title("테스트 시뮬레이션")
                .build();

        // SimulationRepository Mocking
        given(simulationRepository.findAllByUser(user)).willReturn(List.of(simulation));

        // when
        List<SimulationSummaryDto> result = simulationService.findAllSimulationsByUserId(user.getId());

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("테스트 시뮬레이션");
        assertThat(result.get(0).getSimulationId()).isEqualTo(simulation.getId());
    }

//    현재 단건조회는 수정이 필요
//    @Test
//    @DisplayName("사용자가 선택한 simulationId에 해당하는 시뮬레이션 단건 조회 성공")
//    void findSimulationById() {
//
//        User user = User.builder()
//                .id(1L)
//                .email("test@example.com")
//                .password("password")
//                .nickname("testuser")
//                .build();
//
//        Goal goal1 = Goal.builder().id(1L).build();
//
//        //simulationParam(json에 필요한 엔티티)
//        SimulationParams params = SimulationParams.
//                builder()
//                .annualIncomeGrowthRate(0.03)
//                .annualInvestmentReturnRate(0.05)
//                .build();
//
//        //results생성(results에 필요한 필드)
//        SimulationResults results = SimulationResults.builder()
//                .monthlyAssetMap(
//                        new LinkedHashMap<>(Map.of(
//                                "2025-01", 1_000_000L,
//                                "2025-02", 2_000_000L))
//                ).build();
//
//
//        SimulationGoal simGoal = SimulationGoal.builder()
//                .goal(goal1)
//                .build();
//
//        // Simulation 데이터 준비
//        Simulation simulation = Simulation.builder()
//                .id(1L)
//                .user(user) // nickname 필요
//                .title("테스트 시뮬레이션")
//                .baseDate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))
//                .initialAsset(1000000L)
//                .monthlyIncome(3000000L)
//                .monthlyExpense(2000000L)
//                .simulationGoals(List.of(simGoal)) // 적어도 1개는 넣어야 goalIds 에서 NPE 안남
//                .params(params) // null 넣으면 테스트 시 오류날 수 있음
//                .results(results) // null 넣으면 테스트 시 오류날 수 있음
//                .build();
//
//        given(simulationRepository.findById(simulation.getId())).willReturn(Optional.of(simulation));
//
//        // when
//        BaseSimulationResponseDto result = simulationService.findSimulationById(user, simulation.getId());
//
//        //검증
//        assertThat(result.getTitle()).isEqualTo("테스트 시뮬레이션");
//        assertThat(result.getSimulationId()).isEqualTo(simulation.getId());
//        assertThat(result.getBaseDate()).isEqualTo(simulation.getBaseDate());
//        assertThat(result.getInitialAsset()).isEqualTo(simulation.getInitialAsset());
//        assertThat(result.getMonthlyIncome()).isEqualTo(simulation.getMonthlyIncome());
//        assertThat(result.getMonthlyExpense()).isEqualTo(simulation.getMonthlyExpense());
//        assertThat(result.getGoalIds()).containsExactly(goal1.getId());
//        assertThat(result.getParams()).isEqualTo(simulation.getParams());
//        assertThat(result.getResults()).isEqualTo(simulation.getResults());
//
//    }


    @Test
    @DisplayName("save에서 계산로직이 정상적으로 수행")
    void save에서_계산로직이_정상적으로_수행() {
        // given
        long initialAsset = 2_000_000L; //초기 자산
        long monthlyIncome = 2_000_000L; //월 수입
        long monthlyExpense = 1_000_000L; //월 지출
        Long monthlySaving = null; // 자동 계산 월마다 얼마나 저축 (null이면 monthlyIncome - monthlyExpense되게 해놓음)
        double annualInterestRate = 3.0; //연 이자율
        int elapsedMonths = 0; //기준일로부터 경과한 개월 수
        int totalMonths = 12; //전체 시뮬레이션 기간
        LocalDate baseDate = LocalDate.of(2025, 6, 1); //시작 기준일.

        List<Goal> goals = List.of(
                Goal.builder().id(1L).targetAmount(10_000_000L).build()
        );

        // CalculateAll 및 Calculator 직접 생성
        //빈을 주입하면 스프링컨텍슽트(스프링이 관리하는 객체 저장소) 비용증가 가능성이 있다고 함.
        SimulationCalculator calculator = new SimulationCalculator();
        CalculateAll calculateAll = new CalculateAll(calculator);

        // when 시뮬레이션 계산결과들
        SimulationResults results = calculateAll.calculate(
                initialAsset,
                monthlyIncome,
                monthlyExpense,
                monthlySaving,
                annualInterestRate,
                elapsedMonths,
                totalMonths,
                baseDate,
                goals
        );

        // then
        assertThat(results).isNotNull();
        //필요금액 확인
        assertThat(results.getRequiredAmount()).isEqualTo(8_000_000L);
        //목표 달성까지 걸리는 개월 수가 0보다 큼 -> 달성 기간 계산됐는지
        assertThat(results.getMonthsToGoal()).isGreaterThan(0);
        //현재 달성률 0보다 큼.
        assertThat(results.getCurrentAchievementRate()).isGreaterThan(0f);
        //월별 자산 변화 리스트 생성됐는지
        assertThat(results.getMonthlyAssets()).isNotEmpty();
        //달성률 리스트도 잘 생성 됐는지.
        assertThat(results.getMonthlyAchievements()).isNotEmpty();
    }


    @Test
    @DisplayName("사용자가 선택한 simulationId에 해당하는 시뮬레이션 softdelete시뮬레이션 목록 조회 성공")
    void softdelete시뮬레이션_조회가_성공한다() {

        //given
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("password")
                .nickname("testuser")
                .build();

        // Simulation 데이터 준비
        Simulation deletedSimulation = Simulation.builder()
                .id(1L)
                .user(user)
                .title("테스트 시뮬레이션")
                .build();

        deletedSimulation.softDelete();

        // SimulationRepository
        when(simulationRepository.findAllByUserIdAndIsDeletedTrue(user.getId()))
                .thenReturn(List.of(deletedSimulation));

        //when
        List<DeletedSimulationResponseDto> result = simulationService.findAllSoftDeletedSimulations(user.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDeletedAt()).isNotNull();
        assertThat(result.get(0).getSimulationId()).isEqualTo(deletedSimulation.getId());

    }

    @Test
    @DisplayName("잘못된 goalId가 들어왔을 때 SIMULATION_GOAL_NOT_FOUND 예외 발생")
    void saveSimulation은_잘못된id가들어왔을때_CustomException_발생시켜야한다() {
        // given
        Long invalidGoalId = 999L;

        // goalRepository가 빈 리스트를 리턴하도록 설정
        when(goalRepository.findAllById(List.of(invalidGoalId)))
                .thenReturn(List.of());

        LocalDate baseDate = LocalDate.of(2025, 6, 16);

        BaseCreateSimulationRequestDto dto = new BaseCreateSimulationRequestDto(
                "5년 뒤 내 집 마련",
                baseDate,
                1_000_000L,
                3_000_000L,
                2_000_000L,
                1_000_000L,
                3.0,
                0,
                60,
                null, // params 미정 상태(필드 정해지면 다시 테스트)
                List.of(invalidGoalId)
        );

        User user = User.builder().id(1L).build(); // mock user

        // when + then
        assertThrows(CustomException.class, () -> {
            simulationService.saveSimulation(dto, user, List.of(invalidGoalId));
        });
    }

    //batchInsert가 insert가 한 번만 수행되는 것이 맞는지 log로 확인할 수 있음.
    //save에서 batchInsert메서드가 호출이 되는지 확인만 -> 원래 따로 메서드만들어서 테스트하는 것이 좋음.
    @Test
    void saveSimulation은_batchInsert_호출과_DTO반환을_검증한다() {
        //given
        User user2 = User.builder()
                .email("test@example.com")
                .password("password")
                .nickname("testuser")
                .build();

        var savedUser = userRepository.save(user2);


        Long goalId = 1L;
        Goal mockGoal = Goal.builder()
                .id(goalId)
                .user(savedUser) // 꼭 넣어야 함 (nullable = false)
                .title("테스트 목표")
                .category(Category.HOUSING)
                .targetAmount(1_000_000L)
                .startAt(LocalDateTime.now())
                .endAt(LocalDateTime.now().plusMonths(6))
                .progressRate(0f)
                .status(Status.ACTIVE)
                .share(Share.PRIVATE)
                .build();

        // GoalRepository.findAllById()가 goal1 리턴하도록 mock 설정
        when(goalRepository.findAllById(List.of(goalId))).thenReturn(List.of(mockGoal));

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
                null, // params 미정 상태(필드 정해지면 다시 테스트)
                List.of(mockGoal.getId())
        );
        SimulationResults mockResults = SimulationResults.builder()
                .requiredAmount(8_000_000L)
                .monthsToGoal(36)
                .currentAchievementRate(10.0f)
                .monthlyAchievements(List.of()) // 또는 dummy 데이터
                .monthlyAssets(List.of())
                .build();

        when(calculateAll.calculate(
                anyLong(), anyLong(), anyLong(), any(), anyDouble(),
                anyInt(), anyInt(), any(LocalDate.class), anyList()
        )).thenReturn(mockResults);

        // dto에 goalIds에 뭘 넣은거
        System.out.println(dto.getGoalIds());

        // mockGoal에 어떤 id가 들어갔는지
        System.out.println(mockGoal.getId());

        //when
        //내부 구현에 대한 필드를 몰라야한다
        //BaseCreateSimulationRequestDto dto, User user, List<Long> goalIds
        BaseSimulationResponseDto result = simulationService.saveSimulation(
                dto,
                user2,
                List.of(1L) //이거 넘겨줄 때 그냥 goalId가 simulationGoal에 연결되어있는 걸로 가져오느거임.
        );

        //then
        //테스트 중간에 실행되어야 함. 이건 배치 인설트 부분이므로 , 따로 테스트
        //verify(simulationGoalJdbcRepository).batchInsertSimulationGoals(anyList());

        assertEquals("5년 뒤 내 집 마련", result.getTitle());

        assertEquals(1, result.getGoalIds().size());
        assertEquals(1L, result.getGoalIds().getFirst());

        assertNotNull(result.getGoalIds());

    }

//    @Test
//    @DisplayName("시뮬레이션이 목표가 연결되어 있어서 softdelete 삭제 불가능")
//    void 시뮬레이션은_목표가_연결되어있어서_softdelete_삭제가_불가능() {
//        // given
//        User user = mock(User.class);
//        when(user.getId()).thenReturn(1L);
//
//        Simulation simulation = simulationRepository.save(Simulation.builder()
//                .user(user)
//                .title("test simulation")
//                .build());
//
//
//        Goal goal = goalRepository.save(Goal.builder()
//                .title("테스트 목표")
//                .user(user) // 이거 필요하다면 추가
//                .build());
//
//        SimulationGoal simulationGoal = SimulationGoal.builder()
//                .simulation(simulation)
//                .goal(goal)
//                .isActive(true)
//                .linkedAt(LocalDateTime.now())
//                .build();
//
//        simulationGoalRepository.save(simulationGoal);
//
//        // when & then
//        assertThrows(CustomException.class, () -> {
//            simulationService.softDeleteSimulation(user.getId(), simulation.getId());
//        });
//    }

//    @Test
//    @DisplayName("시뮬레이션이 목표가 연결되어 있어서 softdelete삭제 불가능")
//    void 시뮬레이션은_목표가_연결되어있어서_softdelete_삭제가_불가e능() {
//        //given
//        User user = userRepository.save(User.builder().email("test@example.com").build());
//        Simulation simulation = Simulation.builder()
//                .id(1L)
//                .user(user)
//                .title("simulation")
//                .build();
//
//        Goal goal = goalRepository.save(Goal.builder() .title("테스트 목표").build());
//
//
//        SimulationGoal simulationGoal = SimulationGoal.builder()
//                .simulation(simulation)
//                .goal(goal)
//                .isActive(true) // 활성 상태
//                .linkedAt(LocalDateTime.now())
//                .build();
//
//        simulationGoalRepository.save(simulationGoal);
//
//        assertThrows(CustomException.class, () -> {
//            simulationService.softDeleteSimulation(user.getId(), simulation.getId());
//        });
//
//    }


//    @Test
//    @DisplayName("시뮬레이션은 soft delete를 수행")
//    void 시뮬레이션은_softdelete를_수행() {
//
//        // given
//        User user = User.builder()
//                .id(1L)
//                .email("test@example.com")
//                .build();
//
//        Simulation simulation = Simulation.builder()
//                .id(1L)
//                .user(user)
//                .title("simulation")
//                .build();
//
//        given(simulationRepository.save(any(Simulation.class))).willReturn(simulation);
//
//        given(simulationRepository.findById(1L)).willReturn(Optional.of(simulation));
//
//        // stubbing 활성화 됐는지 확인하는 로직.
//        given(simulationGoalRepository.findBySimulationIdAndActiveTrue(simulation.getId()))
//                .willReturn(List.of()); // 활성 목표 없음
//
//        // when
//        DeletedSimulationResponseDto result = simulationService.softDeleteSimulation(user.getId(), simulation.getId());
//
//        // then
//        assertNotNull(result);
//        assertNotNull(result.getDeletedAt());
//        assertEquals(simulation.getId(), result.getSimulationId());
//    }

    @Test
    @DisplayName("시뮬레이션은 완전히 삭제")
    void 시뮬레이션_완전히_삭제() {

        // given
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .build();

        Simulation simulation = Simulation.builder()
                .id(1L)
                .user(user)
                .title("simulation")
                .build();

        given(simulationRepository.save(any(Simulation.class))).willReturn(simulation);

        given(simulationRepository.findById(1L)).willReturn(Optional.of(simulation));

        // stubbing 활성화 됐는지 확인하는 로직.
        given(simulationGoalRepository.findBySimulationIdAndActiveTrue(simulation.getId()))
                .willReturn(List.of()); // 활성 목표 없음

        // when
        DeletedSimulationResponseDto result = simulationService.softDeleteSimulation(user.getId(), simulation.getId());

        // then
        assertNotNull(result);
        assertNotNull(result.getDeletedAt());
        assertEquals(simulation.getId(), result.getSimulationId());

    }




}
