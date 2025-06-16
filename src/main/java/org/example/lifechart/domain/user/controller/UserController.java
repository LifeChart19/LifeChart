package org.example.lifechart.domain.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.lifechart.common.enums.SuccessCode;
import org.example.lifechart.common.response.ApiResponse;
import org.example.lifechart.domain.user.dto.SignupRequest;
import org.example.lifechart.domain.user.dto.SignupResponse;
import org.example.lifechart.domain.user.dto.UserUpdateRequest;
import org.example.lifechart.domain.user.dto.WithdrawalRequest;
import org.example.lifechart.domain.user.entity.User;
import org.example.lifechart.domain.user.service.UserService;
import org.example.lifechart.security.CustomUserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(@Validated @RequestBody SignupRequest request) {
        User user = userService.signup(request);
        SignupResponse response = new SignupResponse(user.getId()); // DTO로 감싼다
        return ApiResponse.onSuccess(SuccessCode.CREATE_USER_SUCCESS, response);    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<Void>> updateProfile(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        userService.updateProfile(userPrincipal.getUserId(), request);
        return ApiResponse.onSuccess(SuccessCode.UPDATE_USER_SUCCESS, null);
    }


    @PatchMapping("/withdrawal")
    public ResponseEntity<ApiResponse<Long>> withdraw(
            @AuthenticationPrincipal CustomUserPrincipal userPrincipal,
            @RequestBody @Valid WithdrawalRequest request
    ) {
        Long userId = userService.withdraw(userPrincipal.getUserId(), request);
        return ApiResponse.onSuccess(SuccessCode.DELETE_USER_SUCCESS, userId);
    }
}
