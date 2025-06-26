package org.example.lifechart.domain.account.dto;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class TransactionResponse {
    private Long id;
    private BigDecimal amount;
    private String type; // DEPOSIT, WITHDRAWAL
    private LocalDateTime createdAt;
    private String description;
    private String memo;
}