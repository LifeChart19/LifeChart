package org.example.lifechart.domain.account.service;

import lombok.RequiredArgsConstructor;
import org.example.lifechart.common.enums.ErrorCode;
import org.example.lifechart.common.exception.CustomException;
import org.example.lifechart.domain.account.dto.AccountResponse;
import org.example.lifechart.domain.account.dto.TransactionResponse;
import org.example.lifechart.domain.account.dto.TransactionStatRequest;
import org.example.lifechart.domain.account.dto.TransactionStatResponse;
import org.example.lifechart.domain.user.service.UserService;
import org.example.lifechart.infra.client.AccountClient;
import org.example.lifechart.infra.client.dto.MockBankApiResponse;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
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
        MockBankApiResponse<List<TransactionResponse>> response = accountClient.getTransactions(userId);
        if (response == null || response.getData() == null) {
            throw new CustomException(ErrorCode.TRANSACTION_NOT_FOUND);
        }
        return response.getData();
    }

    public TransactionStatResponse getUserTransactionStats(Long userId, YearMonth startYM, YearMonth endYM) {
        TransactionStatRequest req = new TransactionStatRequest(startYM, endYM);
        MockBankApiResponse<TransactionStatResponse> response = accountClient.getTransactionStats(userId, req);

        if (response == null || response.getData() == null) {
            throw new CustomException(ErrorCode.TRANSACTION_NOT_FOUND);
        }

        return response.getData();
    }
}
