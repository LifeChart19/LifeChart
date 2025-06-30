package org.example.lifechart.domain.account.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class AccountResponse {
    private Long accountId;
    private Long userId;
    private String accountNumber;
    private BigDecimal balance;
}