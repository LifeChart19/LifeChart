//package org.example.lifechart.domain.simulation.controller;
//
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.security.SecurityRequirement;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.RequiredArgsConstructor;
//import org.example.lifechart.domain.simulation.dto.response.BaseSimulationResponseDto;
//import org.example.lifechart.domain.simulation.dto.response.SimulationSummaryDto;
//import org.example.lifechart.domain.simulation.service.simulation.SimulationService;
//import org.example.lifechart.domain.user.entity.User;
//import org.example.lifechart.global.resolver.LoginUser;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@Tag(name = "Simulation", description = "시뮬레이션 관련 API")
//@RestController
//@RequestMapping("/api")
//@RequiredArgsConstructor
//public class SimulationController {
//
//    private final SimulationService simulationService;
//
//    @Operation(summary = "시뮬레이션 생성", description = "시뮬레이션을 생성합니다.", security = @SecurityRequirement(name = "bearerAuth"))
//    @PostMapping("/simulations")
//    public ResponseEntity<BaseSimulationResponseDto> createSimulation(@RequestBody BaseSimulationResponseDto requestDto,
//                                                                      @LoginUser User user) {
//       BaseSimulationResponseDto response = simulationService.saveSimulation(requestDto, user);
//       return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }
//
//    @Operation(summary = "시뮬레이션 목록 조회", description = "유저의 시뮬레이션 목록을 조회합니다.", security = @SecurityRequirement(name = "bearerAuth"))
//    @GetMapping("/simulations")
//    public ResponseEntity<List<SimulationSummaryDto>> getSimulations(@LoginUser User user) {
//        List<SimulationSummaryDto> simulations = simulationService.findAllSimulationsByUserId(user.getId());
//        return ResponseEntity.ok(simulations);
//    }
//
//    @Operation(summary = "시뮬레이션 상세 조회", description = "시뮬레이션 ID로 상세 정보를 조회합니다.", security = @SecurityRequirement(name = "bearerAuth"))
//    @GetMapping("/simulations/{userId}")
//    public ResponseEntity<BaseSimulationResponseDto> getSimulation(@PathVariable Long id) {
//        BaseSimulationResponseDto simulation = simulationService.findSimulationById(id);
//        return ResponseEntity.ok(simulation);
//    }
//
//    @Operation(summary = "시뮬레이션 업데이트", description = "시뮬레이션을 업데이트합니다.", security = @SecurityRequirement(name = "bearerAuth"))
//    @PutMapping("/simulations/{simulationId}")
//    public ResponseEntity<BaseSimulationResponseDto> updateSimlation(@PathVariable Long simulationId,
//                                                                     @RequestBody BaseSimulationResponseDto requestDto) {
//        BaseSimulationResponseDto simulation = simulationService.updateSimulation(requestDto, user);
//        return ResponseEntity.ok(simulation);
//    }
//
//    @Operation(summary = "시뮬레이션 삭제", description = "시뮬레이션을 삭제합니다.", security = @SecurityRequirement(name = "bearerAuth"))
//    @DeleteMapping("/simulations/{simulationid}")
//    public ResponseEntity<Void> deleteSimulation(@PathVariable Long id) {
//        simulationService.deleteSimulationById(id);
//        return ResponseEntity.ok().build();
//    }
//}
