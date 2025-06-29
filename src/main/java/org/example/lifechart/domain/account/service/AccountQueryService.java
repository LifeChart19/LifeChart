package org.example.lifechart.domain.account.service;

import lombok.RequiredArgsConstructor;
import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.account.dto.AccountResponse;
import org.example.lifechart.domain.account.dto.TransactionResponse;
import org.example.lifechart.domain.user.service.UserService;
import org.example.lifechart.infra.client.AccountClient;
import org.example.lifechart.infra.client.dto.MockBankApiResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountQueryService {

    private final AccountClient accountClient;
    private final UserService userService;

    public AccountResponse getAccount(Long userId) {
        var response = accountClient.getAccount(userId);
        if (response == null || response.getData() == null) {
            throw new CustomException(ErrorCode.ACCOUNT_NOT_FOUND);
        }
        return response.getData();    }

    public List<TransactionResponse> getTransactions(Long userId) {
        // 유저 유효성 검사 (선택, 인증된 userId라면 생략 가능)
        if (!userService.existsById(userId)) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        MockBankApiResponse<List<TransactionResponse>> response = accountClient.getTransactions(userId);

        if (response == null || response.getData() == null) {
            throw new CustomException(ErrorCode.TRANSACTION_NOT_FOUND);
        }

        return response.getData();
    }

}
