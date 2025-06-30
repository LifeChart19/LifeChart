package org.example.lifechart.infra.client.dto;

import lombok.Getter;

@Getter
public class MockBankApiResponse<T> {
    private boolean success;
    private String code;
    private String message;
    private T data;
}
