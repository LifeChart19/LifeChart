package org.example.lifechart.domain.notification.controller;

import java.util.List;

import org.example.lifechart.common.enums.SuccessCode;
import org.example.lifechart.common.response.ApiResponse;
import org.example.lifechart.domain.notification.dto.NotificationCreateRequestDto;
import org.example.lifechart.domain.notification.dto.NotificationResponseDto;
import org.example.lifechart.domain.notification.service.NotificationCreateService;
import org.example.lifechart.domain.notification.service.NotificationService;
import org.example.lifechart.security.CustomUserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/notification")
public class NotificationController {


	private final NotificationService notificationService;
	private final NotificationCreateService notificationCreateService;

	@GetMapping
	public ResponseEntity<ApiResponse<List<NotificationResponseDto>>> getList(
		@RequestParam(defaultValue = "0") Long cursor,
		@AuthenticationPrincipal CustomUserPrincipal userDetails
	){

		return ApiResponse.onSuccess(SuccessCode.GET_NOTIFICATIONS_SUCCESS,
			notificationService.getList(cursor, userDetails.getUserId()));
	}

	@PatchMapping
	public ResponseEntity<ApiResponse<Void>> patchAll(
		@AuthenticationPrincipal CustomUserPrincipal userDetails
	){
		notificationService.patchAll(userDetails.getUserId());
		return ApiResponse.onSuccess(SuccessCode.PATCH_ALL_NOTIFICATIONS_SUCCESS,
			null);
	}

	@GetMapping("{notificationId}")
	public ResponseEntity<ApiResponse<NotificationResponseDto>> get(
		@PathVariable Long notificationId,
		@AuthenticationPrincipal CustomUserPrincipal userDetails
	){
		return ApiResponse.onSuccess(SuccessCode.GET_NOTIFICATION_SUCCESS,
			notificationService.get(notificationId, userDetails.getUserId()));
	}

	@PatchMapping("{notificationId}")
	public ResponseEntity<ApiResponse<Void>> patch(
		@PathVariable Long notificationId,
		@AuthenticationPrincipal CustomUserPrincipal userDetails

	){
		notificationService.patch(notificationId, userDetails.getUserId());
		return ApiResponse.onSuccess(SuccessCode.PATCH_NOTIFICATION_SUCCESS,
			null);
	}

	@PostMapping()
	public ResponseEntity<ApiResponse<Void>> create(
		@AuthenticationPrincipal CustomUserPrincipal userDetails,
		@RequestBody NotificationCreateRequestDto dto
	){

		notificationCreateService.create(dto);

		return null;
	}


}
