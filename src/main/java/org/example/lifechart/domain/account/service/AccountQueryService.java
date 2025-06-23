package org.example.lifechart.domain.account.service;

import lombok.RequiredArgsConstructor;
import org.example.lifechart.domain.account.dto.AccountResponse;
import org.example.lifechart.domain.account.dto.TransactionResponse;
import org.example.lifechart.infra.client.AccountClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountQueryService {

    private final AccountClient accountClient;

    public AccountResponse getAccount(Long userId) {
        return accountClient.getAccount(userId);
    }

    public List<TransactionResponse> getTransactions(Long userId) {
        return accountClient.getTransactions(userId);
    }
}
