package org.example.lifechart.domain.simulation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.lifechart.common.enums.SuccessCode;
import org.example.lifechart.common.response.ApiResponse;
import org.example.lifechart.domain.simulation.dto.request.BaseCreateSimulationRequestDto;
import org.example.lifechart.domain.simulation.dto.request.UpdateSimulationRequestDto;
import org.example.lifechart.domain.simulation.dto.response.BaseSimulationResponseDto;
import org.example.lifechart.domain.simulation.dto.response.CreateSimulationResponseDto;
import org.example.lifechart.domain.simulation.dto.response.DeletedSimulationResponseDto;
import org.example.lifechart.domain.simulation.dto.response.SimulationSummaryDto;
import org.example.lifechart.domain.simulation.service.simulation.SimulationService;
import org.example.lifechart.security.CustomUserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "Simulation", description = "시뮬레이션 관련 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SimulationController {

    private final SimulationService simulationService;

    //시뮬레이션 생성 로직
    @Operation(summary = "시뮬레이션 생성", description = "시뮬레이션을 생성합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/simulations")
    public ResponseEntity<ApiResponse<CreateSimulationResponseDto>> createSimulation(
            @Valid @RequestBody BaseCreateSimulationRequestDto requestDto,
            @AuthenticationPrincipal CustomUserPrincipal principal) {

        CreateSimulationResponseDto response = simulationService.saveSimulation(requestDto, principal.getUserId(), requestDto.getGoalIds());
        return ApiResponse.onSuccess(SuccessCode.SIMULATION_CREATE_SUCCESS, response);
    }

    //시뮬레이션 목록 조회 로직
    @Operation(summary = "시뮬레이션 목록 조회", description = "유저의 시뮬레이션 목록을 조회합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/simulations")
    public ResponseEntity<ApiResponse<List<SimulationSummaryDto>>> getSimulations(@AuthenticationPrincipal CustomUserPrincipal principal) {
        List<SimulationSummaryDto> simulations = simulationService.findAllSimulationsByUserId(principal.getUserId());
        return ApiResponse.onSuccess(SuccessCode.SIMULATION_GET_LIST_SUCCESS, simulations);
    }

    //시뮬레이션 상세 조회 로직
    @Operation(summary = "시뮬레이션 상세 조회", description = "시뮬레이션 ID로 상세 정보를 조회합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/simulations/{simulationId}")
    public ResponseEntity<ApiResponse<BaseSimulationResponseDto>> getSimulation(@AuthenticationPrincipal CustomUserPrincipal principal, @PathVariable Long simulationId) {
        BaseSimulationResponseDto simulation = simulationService.findSimulationByUserIdAndSimulationId(principal.getUserId(), simulationId);
        return ApiResponse.onSuccess(SuccessCode.SIMULATION_GET_LIST_SUCCESS, simulation);
    }

    //시뮬레이션 softdelete내역 조회 로직
    @Operation(summary = "시뮬레이션 softdelete 삭제된 내역들 조회", description = "시뮬레이션 ID로 삭제된 정보를 조회합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/simulations/deleted")
    public ResponseEntity<ApiResponse<List<DeletedSimulationResponseDto>>> getSoftSimulation(@AuthenticationPrincipal CustomUserPrincipal principal) {
        List<DeletedSimulationResponseDto> simulations = simulationService.findAllSoftDeletedSimulations(principal.getUserId());
        return ApiResponse.onSuccess(SuccessCode.SIMULATION_GET_DELETED_LIST_SUCCESS, simulations);
    }

    @Operation(summary = "시뮬레이션 안에서 업데이트", description = "시뮬레이션에서 목표에 해당하는 계산로직을 수행합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @PatchMapping("/simulations/{simulationId}")
    public ResponseEntity<ApiResponse<CreateSimulationResponseDto>> updateSimulation(@AuthenticationPrincipal CustomUserPrincipal principal, @PathVariable Long simulationId, @RequestBody UpdateSimulationRequestDto requestDto) {
        CreateSimulationResponseDto simulation = simulationService.updateSimulationSettings(principal.getUserId(), simulationId, requestDto.getGoalIds() );
        return ApiResponse.onSuccess(SuccessCode.SIMULATION_PATCH_SUCCESS, simulation);
    }

    //업데이트 로직은 컨트롤러 추후 수정필요.
    @Operation(summary = "시뮬레이션 업데이트", description = "시뮬레이션을 업데이트합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/goals/{goalId}/simulations/recalculate")
    public  ResponseEntity<ApiResponse<Void>> updateSimulation(@AuthenticationPrincipal CustomUserPrincipal principal, @PathVariable Long goalId) {
        simulationService.updateSimulationsByGoalChange(principal.getUserId(), goalId);
        return ApiResponse.onSuccess(SuccessCode.SIMULATION_PATCH_SUCCESS, null);
    }

    //시뮬레이션 소프트딜리트로 삭제하는 로직
    @Operation(summary = "시뮬레이션 소프트딜리트 삭제", description = "시뮬레이션을 소프트 딜리트 방식으로 삭제합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @PatchMapping("/simulations/{simulationId}/soft-delete")
    public ResponseEntity<ApiResponse<DeletedSimulationResponseDto>> softDeleteSimulation(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long simulationId) {
        DeletedSimulationResponseDto simulation = simulationService.softDeleteSimulation(principal.getUserId(), simulationId);
        return ApiResponse.onSuccess(SuccessCode.SIMULATION_SOFT_DELETE_SUCCESS, simulation);
    }

    //시뮬레이션 완전삭제 로직
    @Operation(summary = "시뮬레이션 완전 삭제", description = "시뮬레이션을 완전히 삭제합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/simulations/{simulationId}")
    public ResponseEntity<ApiResponse<Void>> deleteSimulation(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long simulationId) {
        simulationService.deleteSimulation(principal.getUserId(), simulationId);
        return ApiResponse.onSuccess(SuccessCode.SIMULATION_DELETE_SUCCESS, null);
    }
}

