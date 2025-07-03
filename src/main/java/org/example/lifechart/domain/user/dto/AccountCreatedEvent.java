package org.example.lifechart.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccountCreatedEvent {
    private Long userId;
    private String email;
    private String userName;
    private BigDecimal salary;
    private String createdAt;
}