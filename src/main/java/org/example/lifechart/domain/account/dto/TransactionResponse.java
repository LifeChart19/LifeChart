package org.example.lifechart.domain.account.dto;

import lombok.Getter;
import org.example.lifechart.domain.account.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class TransactionResponse {
    private Long id;
    private BigDecimal amount;
    private TransactionType type; // DEPOSIT, WITHDRAWAL
    private LocalDateTime createdAt;
    private String description;
    private String memo;
}