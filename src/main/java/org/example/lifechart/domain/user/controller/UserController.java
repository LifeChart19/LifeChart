package org.example.lifechart.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.lifechart.common.enums.SuccessCode;
import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.response.ApiResponse;
import org.example.lifechart.domain.user.dto.SignupRequest;
import org.example.lifechart.domain.user.dto.SignupResponse;
import org.example.lifechart.domain.user.entity.User;
import org.example.lifechart.domain.user.service.UserService;
import org.example.lifechart.common.exception.CustomException;
import org.springframework.http.ResponseEntity;
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
        SignupResponse response = new SignupResponse(user.getId());

        return ApiResponse.onSuccess(SuccessCode.CREATE_USER_SUCCESS, response);
    }
}
