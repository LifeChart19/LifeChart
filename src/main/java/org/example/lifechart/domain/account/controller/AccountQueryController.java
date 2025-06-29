package org.example.lifechart.domain.account.controller;

import lombok.RequiredArgsConstructor;
import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.enums.SuccessCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.common.response.ApiResponse;
import org.example.lifechart.domain.account.dto.AccountResponse;
import org.example.lifechart.domain.account.dto.TransactionResponse;
import org.example.lifechart.domain.account.service.AccountQueryService;
import org.example.lifechart.security.CustomUserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/lifechart/accounts")
public class AccountQueryController {

    private final AccountQueryService accountQueryService;

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<AccountResponse>> getAccount(@PathVariable Long userId, @AuthenticationPrincipal CustomUserPrincipal principal) {

        // 권한 체크 (본인만 가능)
        if (!principal.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.NO_AUTHORIZATION);
        }
        AccountResponse response = accountQueryService.getAccount(userId);
        return ApiResponse.onSuccess(SuccessCode.GET_ACCOUNT_SUCCESS, response);
    }

    @GetMapping("/{userId}/transactions")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactions(@PathVariable Long userId,
                                                                                  @AuthenticationPrincipal CustomUserPrincipal principal) {

        // 권한 체크 (본인만 가능)
        if (!principal.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.NO_AUTHORIZATION);
        }
        List<TransactionResponse> list = accountQueryService.getTransactions(userId);
        return ApiResponse.onSuccess(SuccessCode.GET_TRANSACTIONS_SUCCESS, list);
    }
}


