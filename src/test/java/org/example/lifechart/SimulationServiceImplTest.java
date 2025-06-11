package org.example.lifechart;

import org.example.lifechart.domain.simulation.dto.request.BaseCreateSimulationRequestDto;
import org.example.lifechart.domain.simulation.dto.response.BaseSimulationResponseDto;
import org.example.lifechart.domain.simulation.dto.response.SimulationSummaryDto;
import org.example.lifechart.domain.simulation.entity.Simulation;
import org.example.lifechart.domain.simulation.repository.SimulationRepository;
import org.example.lifechart.domain.simulation.service.simulation.SimulationServiceImpl;
import org.example.lifechart.domain.user.entity.User;
import org.example.lifechart.domain.user.repository.UserRepository;
import org.example.lifechart.domain.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class SimulationServiceImplTest {


    @InjectMocks
    private SimulationServiceImpl simulationService;

    @Mock
    private UserService userService;

    @Mock
    private SimulationRepository simulationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimulationRepository simulationRepository;

//    @Autowired
//    private SimulationGoal simulationRepository;

    //독립적으로 사용 해야한다면, 이건 너무 공통테스트에 의존적이 된다. 웬만하면 사용하지 말 것.
    //@BeforeEach
    //@BeforeAll
    void setUp() {
        User user = User.builder()
                .id(1L) // getId할거 없으면 nullpointerException발생
                .email("test@example.com")
                .password("password")
                .nickname("testuser")
                .age(30)
                .build();

        Simulation simulation = Simulation.builder()
                .id(1L)
                .user(user)
                .title("테스트 시뮬레이션")
                .build();

        // UserService Mock 동작 지정
        when(userService.findByIdOrElseThrow(1L)).thenReturn(user);

        // SimulationRepository Mock 동작 지정 테스트 코드에서 findAllByUser호출시 list가 반환.
        when(simulationRepository.findAllByUser(user)).thenReturn(List.of(simulation));

    }

    @Test
    @DisplayName("사용자 id로 Simulation 전체 목록 조회 성공")
    void findAllSimulationsByUserId() {
        //give
        User user = User.builder()
                .email("test@example.com")
                .password("password")
                .nickname("testuser")
                .age(30)
                .build();

        var createdUser = userRepository.save(user);

        Simulation simulation = Simulation.builder()
                .id(1L)
                .user(user)
                .title("테스트 시뮬레이션")
                .build();

        var createdSimulation = simulationRepository.save(
                new Simulation(
                        null,
                        createdUser,
                        "",
                        //...
                )
        );

        @Test
        @DisplayName("사용자 id로 Simulation 전체 목록 조회 성공")
        void saveSimulation은_잘못된값이왓을대_XXXException_발생시켜야한다() {
            //given
            User user2 = User.builder()
                    .email("test@example.com")
                    .password("password")
                    .nickname("testuser")
                    .age(30)
                    .build();

            Simulation simulation2 = Simulation.builder()
                    .id(1L)
                    .user(user)
                    .title("테스트 시뮬레이션")
                    .build();


//            BaseCreateSimulationRequestDto dto, User user, List<Long> goalIds

            var dto = new BaseCreateSimulationRequestDto(
                    //...
            );



        //when
        //내부 구현에 대한 필드를 몰라야한다
        assertThrows<Invalid> ();

        //then
            var simulations = simulationRepository.findAll();
            var simulationGoals = simulationRepository.findAll();

            assertEquals(1, simulations.size());
            assertEquals(1, simulationGoals.size());
    }

        @Test
        @DisplayName("사용자 id로 Simulation 전체 목록 조회 성공")
        void saveSimulation은_검증을_올바르게_수행한다() {
            //given
            User user2 = User.builder()
                    .email("test@example.com")
                    .password("password")
                    .nickname("testuser")
                    .age(30)
                    .build();

            Simulation simulation2 = Simulation.builder()
                    .id(1L)
                    .user(user)
                    .title("테스트 시뮬레이션")
                    .build();


//            BaseCreateSimulationRequestDto dto, User user, List<Long> goalIds

            var dto = new BaseCreateSimulationRequestDto(
                    //...
            );



            //when
            //내부 구현에 대한 필드를 몰라야한다
            BaseSimulationResponseDto result = simulationService.saveSimulation(
                    dto,
                    user2,
                    List.of(1)
            );

            //then
            var simulations = simulationRepository.findAll();
            var simulationGoals = simulationRepository.findAll();

            assertEquals(1, simulations.size());
            assertEquals(1, simulationGoals.size());
        }

    @Test
    @DisplayName("Simulation 목록 단건 조회 성공")
    void findSimulationById() {

        //given

        //when
        BaseSimulationResponseDto simulationResponseDto = simulationService.findSimulationById(1L);
        when(simulationRepository.findById(1L).orElseThrow(RuntimeException::new));

        //then
        assertNotNull(simulationResponseDto.getSimulationId());


    }


}
