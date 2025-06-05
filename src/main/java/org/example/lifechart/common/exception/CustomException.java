package org.example.lifechart.common.exception;

import org.example.lifechart.common.enums.BaseCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomException extends RuntimeException {

    private final BaseCode errorCode;

}
