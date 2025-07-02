package org.example.lifechart.domain.account.controller;

import lombok.RequiredArgsConstructor;
import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.enums.SuccessCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.common.response.ApiResponse;
import org.example.lifechart.domain.account.dto.AccountResponse;
import org.example.lifechart.domain.account.dto.TransactionResponse;
import org.example.lifechart.domain.account.dto.TransactionStatRequest;
import org.example.lifechart.domain.account.dto.TransactionStatResponse;
import org.example.lifechart.domain.account.service.AccountQueryService;
import org.example.lifechart.security.CustomUserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


import java.time.YearMonth;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/lifechart/accounts")
public class AccountQueryController {

    private final AccountQueryService accountQueryService;

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<AccountResponse>> getAccount(@PathVariable Long userId) {
        AccountResponse response = accountQueryService.getAccount(userId);
        return ApiResponse.onSuccess(SuccessCode.GET_ACCOUNT_SUCCESS, response);
    }

    @GetMapping("/{userId}/transactions")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactions(@PathVariable Long userId) {
        List<TransactionResponse> list = accountQueryService.getTransactions(userId);
        return ApiResponse.onSuccess(SuccessCode.GET_TRANSACTIONS_SUCCESS, list);
    }

    @PostMapping("/{userId}/transactions/stats")
    public ResponseEntity<ApiResponse<TransactionStatResponse>> getUserTransactionStats(
            @PathVariable Long userId,
            @RequestBody TransactionStatRequest request
    ) {
        TransactionStatResponse resp = accountQueryService.getUserTransactionStats(
                userId,
                request.getStartYM(),
                request.getEndYM()
        );
        return ApiResponse.onSuccess(SuccessCode.GET_TRANSACTION_STATS_SUCCESS, resp);
    }
}



