package org.example.lifechart.infra.client;

import org.example.lifechart.domain.account.dto.AccountResponse;
import org.example.lifechart.domain.account.dto.TransactionResponse;
import org.example.lifechart.infra.client.dto.MockBankApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(
        name = "accountClient",
        url = "${mock-bank.url}",
        configuration = org.example.lifechart.common.config.FeignConfig.class
)
public interface AccountClient {
    @GetMapping("/accounts/{userId}")
    MockBankApiResponse<AccountResponse> getAccount(@PathVariable("userId") Long userId);

    @GetMapping("/accounts/{userId}/transactions")
    MockBankApiResponse<List<TransactionResponse>> getTransactions(@PathVariable("userId") Long userId);
}

