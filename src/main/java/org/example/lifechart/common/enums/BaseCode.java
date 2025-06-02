package org.example.lifechart.common.enums;

import org.springframework.http.HttpStatus;

public interface BaseCode {

    HttpStatus getStatus();

    String getMessage();
}
