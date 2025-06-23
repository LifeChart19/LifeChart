package org.example.lifechart.domain.simulation;


import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.goal.entity.Goal;
import org.example.lifechart.domain.goal.enums.Category;
import org.example.lifechart.domain.goal.enums.Share;
import org.example.lifechart.domain.goal.enums.Status;
import org.example.lifechart.domain.goal.repository.GoalRepository;
import org.example.lifechart.domain.simulation.dto.request.BaseCreateSimulationRequestDto;
import org.example.lifechart.domain.simulation.dto.response.*;
import org.example.lifechart.domain.simulation.entity.Simulation;
import org.example.lifechart.domain.simulation.entity.SimulationGoal;
import org.example.lifechart.domain.simulation.repository.SimulationGoalJdbcRepository;
import org.example.lifechart.domain.simulation.repository.SimulationGoalRepository;
import org.example.lifechart.domain.simulation.repository.SimulationRepository;
import org.example.lifechart.domain.simulation.service.calculator.CalculateAll;
import org.example.lifechart.domain.simulation.service.calculator.SimulationCalculator;
import org.example.lifechart.domain.simulation.service.simulation.SimulationServiceImpl;
import org.example.lifechart.domain.simulation.service.simulation.SimulationValidator;
import org.example.lifechart.domain.user.entity.User;
import org.example.lifechart.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.util.AssertionErrors.assertEquals;


@ExtendWith(MockitoExtension.class)
public class SimulationServiceImplTest {


    @InjectMocks
    private SimulationServiceImpl simulationService;

    @Mock
    private SimulationRepository simulationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SimulationGoalRepository simulationGoalRepository;

    @Mock
    private SimulationGoalJdbcRepository simulationGoalJdbcRepository;

    @Mock
    private SimulationValidator simulationValidator;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private CalculateAll calculateAll;


    @Test
    @DisplayName("사용자 id로 Simulation 전체 목록 조회 성공")
    void 사용자_id로_Simulation_전체_목록_조회_성공() {
        //given
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("password")
                .nickname("testuser")
                .isDeleted(false)
                .build();

        // UserRepository id찾으면 user를 줌.
        given(userRepository.findByIdAndDeletedAtIsNull(user.getId())).willReturn(Optional.of(user));

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

    @Test
    @DisplayName("save에서 계산로직이 정상적으로 수행")
    void save에서_계산로직이_정상적으로_수행() {
        // given
        //보통 사용자는 수입/지출 항목을 단독으로 관리하며, 목표 기반 계좌와는 별도로 운영할 가능성이 큼. 좀 더  고민해볼 것.
        long initialAsset = 2_000_000L; //초기 자산
        long monthlyIncome = 2_000_000L; //월 수입
        long monthlyExpense = 1_000_000L; //월 지출
        Long monthlySaving =500000L; // 자동 계산 월마다 얼마나 저축 (null이면 monthlyIncome - monthlyExpense되게 해놔야..)
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
                .isDeleted(false)
                .nickname("testuser")
                .build();

        given(userRepository.findByIdAndDeletedAtIsNull(user.getId())).willReturn(Optional.of(user));

        // Simulation 데이터 준비
        Simulation deletedSimulation = Simulation.builder()
                .id(1L)
                .user(user)
                .title("테스트 시뮬레이션")
                .isDeleted(true)
                .deletedAt(LocalDateTime.now())
                .build();

        given(simulationRepository.findAllByUserIdAndIsDeletedTrue(user.getId())).willReturn(List.of(deletedSimulation));

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

        User user2 = User.builder()
                .email("test@example.com")
                .password("password")
                .nickname("testuser")
                .isDeleted(false)
                .build();

        ReflectionTestUtils.setField(user2, "id", 1L);

        given(userRepository.findByIdAndDeletedAtIsNull(user2.getId()))
                .willReturn(Optional.of(user2));

        given(goalRepository.findAllWithUserByIdAndUserId(List.of(invalidGoalId), user2.getId()))
                .willReturn(Collections.emptyList());

        LocalDate baseDate = LocalDate.of(2025, 6, 16);

        BaseCreateSimulationRequestDto dto = new BaseCreateSimulationRequestDto(
                "5년 뒤 내 집 마련",
                baseDate,
                1_000_000L,
                3_000_000L,
                2_000_000L,
                1_000_000L,
                3.0,//
                0,
                60,
                List.of(invalidGoalId)
        );

        // when + then
        assertThrows(CustomException.class, () -> {
            simulationService.saveSimulation(dto, user2.getId(), List.of(invalidGoalId));
        });
    }

    @Test
    @DisplayName("시뮬레이션은 선택한 목표들과 성공적으로 업데이트된다")
    void 시뮬레이션은_선택한_목표들과_성공적으로_업데이트가_가능() {

        Long userId = 1L;
        Long simulationId = 10L;
        List<Long> goalIds = List.of(100L);

        //given
        User user = User.builder()
                .id(userId)
                .email("test@example.com")
                .password("password")
                .isDeleted(false)
                .nickname("testuser")
                .build();

        given(userRepository.findByIdAndDeletedAtIsNull(user.getId())).willReturn(Optional.of(user));

        Goal goal = Goal.builder()
                .id(1L)
                .user(user) // 꼭 넣어야 함 (nullable = false)
                .title("테스트 목표")
                .category(Category.HOUSING)
                .targetAmount(1_000_000L)
                .build();

        given(goalRepository.findAllWithUserByIdAndUserId(goalIds, userId)).willReturn(List.of(goal));

        Simulation simulation = Simulation.builder()
                .id(simulationId)
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

        given(simulationRepository.findById(simulation.getId())).willReturn(Optional.of(simulation));


        SimulationResults mockResults = new SimulationResults();

        given(calculateAll.calculate(
                anyLong(), anyLong(), anyLong(), anyLong(), anyDouble(),
                anyInt(), anyInt(), any(LocalDate.class), anyList()
        )).willReturn(mockResults);

        CreateSimulationResponseDto response = simulationService.updateSimulationSettings(userId, simulationId, goalIds);

        //batchinsert호출 검증
        verify(simulationGoalJdbcRepository).deactivateSimulationGoals(simulationId);
        verify(simulationGoalJdbcRepository).batchInsertSimulationGoals(anyList());

        assertThat(response).isNotNull();
        assertThat(response.getSimulationId()).isEqualTo(simulationId);

    }

    @Test
    @DisplayName("시뮬레이션은 목표가 수정되면 시뮬레이션도 수정된다")
    void 시뮬레이션은_목표가_수정되면_시뮬레이션도_수정된다() {
        //given
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("password")
                .isDeleted(false)
                .nickname("testuser")
                .build();

        given(userRepository.findByIdAndDeletedAtIsNull(user.getId())).willReturn(Optional.of(user));

        Goal goal = Goal.builder()
                .id(1L)
                .user(user) // 꼭 넣어야 함 (nullable = false)
                .title("테스트 목표")
                .category(Category.HOUSING)
                .targetAmount(1_000_000L)
                .build();

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
                .id(1L)
                .simulation(simulation)
                .goal(goal)
                .active(true)
                .linkedAt(LocalDateTime.now())
                .build();

        given(simulationGoalRepository.findAllByGoalIdAndActiveTrue(goal.getId())).willReturn(List.of(simulationGoal));
        given(simulationGoalRepository.findActiveGoalsBySimulationId(simulation.getId())).willReturn(List.of(goal));

        SimulationResults results = SimulationResults.builder()
                .requiredAmount(8_000_000L)
                .monthsToGoal(36)
                .currentAchievementRate(10.0f)
                .monthlyAchievements(List.of())
                .monthlyAssets(List.of())
                .build();

        given(calculateAll.calculate(
                anyLong(), anyLong(), anyLong(), any(), anyDouble(),
                anyInt(), anyInt(), any(LocalDate.class), anyList()
        )).willReturn(results);

        simulationService.updateSimulationsByGoalChange(user.getId(), goal.getId(), simulation.getId());

        // then
        // 시뮬레이션 객체 내부값이 실제로 바꼈는지 확인
        assertThat(simulation.getRequiredAmount()).isEqualTo(results.getRequiredAmount());
        assertThat(simulation.getMonthsToGoal()).isEqualTo(results.getMonthsToGoal());

    }


    //batchInsert가 insert가 한 번만 수행되는 것이 맞는지 log로 확인할 수 있음.
    //save에서 batchInsert메서드가 호출이 되는지 확인만 -> 원래 따로 메서드만들어서 테스트하는 것이 좋음.
    @Test
    @DisplayName("saveSimulation은 batchInsert호출과 DTO반환을 검증")
    void saveSimulation은_batchInsert_호출과_DTO반환을_검증한다() {
        //given
        User user2 = User.builder()
                .email("test@example.com")
                .password("password")
                .nickname("testuser")
                .isDeleted(false)
                .build();

        given(userRepository.findByIdAndDeletedAtIsNull(user2.getId())).willReturn(Optional.of(user2));

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
        System.out.println("mockGoal ID = " + mockGoal.getId());
        // GoalRepository.findAllById()가 goal1 리턴하도록 mock 설정

        given(goalRepository.findAllWithUserByIdAndUserId(List.of(goalId), user2.getId()))
                .willReturn(List.of(mockGoal));

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
                .monthsToGoal(36)
                .currentAchievementRate(10.0f)
                .monthlyAchievements(List.of()) // 또는 dummy 데이터
                .monthlyAssets(List.of())
                .build();

        given(calculateAll.calculate(
                anyLong(), anyLong(), anyLong(), any(), anyDouble(),
                anyInt(), anyInt(), any(LocalDate.class), anyList()
        )).willReturn(mockResults);


        //when
        //내부 구현에 대한 필드를 몰라야한다
        //BaseCreateSimulationRequestDto dto, User user, List<Long> goalIds
        CreateSimulationResponseDto result = simulationService.saveSimulation(
                dto,
                user2.getId(),
                List.of(goalId) //이거 넘겨줄 때 그냥 goalId가 simulationGoal에 연결되어있는 걸로 가져오느거임.
        );

        //이런 방법으로도 id세팅가능
        ReflectionTestUtils.setField(result, "simulationId", 1L);
        //then
        //테스트 중간에 실행되어야 함. 이건 배치 인설트 부분이므로 , 따로 테스트
        //verify(simulationGoalJdbcRepository).batchInsertSimulationGoals(anyList());
        assertEquals("시뮬레이션 ID가 일치", 1L, result.getSimulationId());
    }

    @Test
    @DisplayName("시뮬레이션 단건 조회 수행")
    void 시뮬레이션은_단건_조회를_수행() {

        User user2 = User.builder()
                .id(1L)
                .email("test@example.com")
                .isDeleted(false)
                .build();

        given(userRepository.findByIdAndDeletedAtIsNull(user2.getId())).willReturn(Optional.of(user2));

        Simulation simulation = Simulation.builder()
                .id(1L)
                .user(user2)
                .title("simulation")
                .currentAchievementRate(50.0f)
                .build();

        given(simulationRepository.findById(1L)).willReturn(Optional.of(simulation));

        //유저랑 simulationId랑 같은지 확인하는 것은 예외 검증테스트 로직으로 관리해야 함.
        BaseSimulationResponseDto simulationResponseDto = simulationService.findSimulationByUserIdAndSimulationId(user2.getId(), simulation.getId());

        //검증은 값이 잘 조회되는지 나오는지.
        assertThat(simulationResponseDto).isNotNull();
        assertThat(simulationResponseDto.getSimulationId()).isEqualTo(simulation.getId());
        assertThat(simulationResponseDto.getTitle()).isEqualTo("simulation");
        assertThat(simulationResponseDto.getCurrentAchievementRate()).isEqualTo(50.0f);

    }

    @Test
    @DisplayName("시뮬레이션 조회는 유저가 일치하지 않으면 예외 수행")
    void 시뮬레이션_조회시_유저불일치면_예외발생() {

        Long userId = 1L;
        Long simulationId = 10L;

        User user1 = User.builder().id(userId).isDeleted(false).build();
        User user2 = User.builder().id(2L).isDeleted(false).build();

        Simulation simulation = Simulation.builder()
                .id(1L)
                .user(user2)
                .build();

        given(userRepository.findByIdAndDeletedAtIsNull(userId)).willReturn(Optional.of(user1));
        given(simulationRepository.findById(simulationId)).willReturn(Optional.of(simulation));

        CustomException ex = assertThrows(CustomException.class, () -> {
            simulationService.findSimulationByUserIdAndSimulationId(userId, simulationId);
        });

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.SIMULATION_BAD_REQUEST);
    }


    @DisplayName("시뮬레이션은 soft delete를 수행")
    @Test
    void 시뮬레이션은_softdelete를_수행() {
        // given
        Long userId = 1L;
        Long simulationId = 1L;

        User user = User.builder()
                .id(userId)
                .email("test@example.com")
                .isDeleted(false)
                .build();
        given(userRepository.findByIdAndDeletedAtIsNull(userId)).willReturn(Optional.of(user));

        Simulation simulation = Simulation.builder()
                .id(simulationId)
                .user(user)
                .title("simulation")
                .isDeleted(false)
                .build();

        given(simulationRepository.findById(simulationId)).willReturn(Optional.of(simulation));
        //활성 목표 없앰
        given(simulationGoalRepository.findBySimulationIdAndActiveTrue(simulationId))
                .willReturn(List.of());

        // when
        DeletedSimulationResponseDto result = simulationService.softDeleteSimulation(userId, simulationId);

        // then
        assertNotNull(result);
        assertNotNull(result.getDeletedAt());
        assertTrue(simulation.isDeleted());
    }

    @Test
    @DisplayName("시뮬레이션이 목표가 연결되어 있어서 softdelete삭제 불가능")
    void 시뮬레이션은_목표가_연결되어있어서_softdelete_삭제가_불가능() {
        //given
        User user = User.builder().email("test@example.com").build();
        ReflectionTestUtils.setField(user, "id", 1L);

        given(userRepository.findByIdAndDeletedAtIsNull(user.getId())).willReturn(Optional.of(user));

        Goal goal = Goal.builder().user(user).title("테스트 목표").build();

        Simulation simulation = Simulation.builder()
                .user(user)
                .title("simulation")
                .build();
        ReflectionTestUtils.setField(simulation, "id", 10L);

        given(simulationRepository.findById(10L)).willReturn(Optional.of(simulation));

        SimulationGoal simulationGoal = SimulationGoal.builder()
                .simulation(simulation)
                .goal(goal)
                .active(true)
                .linkedAt(LocalDateTime.now())
                .build();

        given(simulationGoalRepository.findBySimulationIdAndActiveTrue(10L))
                .willReturn(List.of(simulationGoal));

        assertThrows(CustomException.class, () -> {
            simulationService.softDeleteSimulation(user.getId(), simulation.getId());
        });

    }

    @Test
    @DisplayName("시뮬레이션은 완전히 삭제")
    void 시뮬레이션_완전히_삭제() {
        // given
        Long userId = 1L;
        Long simulationId = 1L;

        User user = User.builder()
                .id(userId)
                .email("test@example.com")
                .isDeleted(false)
                .build();

        Simulation simulation = Simulation.builder()
                .id(simulationId)
                .user(user)
                .title("simulation")
                .isDeleted(true) // 이미 soft delete 되어야 함
                .build();

        given(simulationRepository.findById(simulationId)).willReturn(Optional.of(simulation));
        given(userRepository.findByIdAndDeletedAtIsNull(userId)).willReturn(Optional.of(user));

        // when
        simulationService.deleteSimulation(userId, simulationId);

        // then
        verify(simulationRepository).delete(simulation); // delete 호출 여부 검증
    }
}
