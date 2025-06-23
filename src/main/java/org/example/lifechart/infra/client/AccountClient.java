package org.example.lifechart.infra.client;

import org.example.lifechart.domain.account.dto.AccountResponse;
import org.example.lifechart.domain.account.dto.TransactionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "accountClient", url = "${mock-bank.url}")
public interface AccountClient {

    @GetMapping("/accounts/{userId}")
    AccountResponse getAccount(@PathVariable("userId") Long userId);

    @GetMapping("/accounts/{userId}/transactions")
    List<TransactionResponse> getTransactions(@PathVariable("userId") Long userId);
}
