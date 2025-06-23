package org.example.lifechart.domain.account.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class AccountInfoResponse {
    private String accountNumber;
    private String accountName;
    private BigDecimal balance;
}