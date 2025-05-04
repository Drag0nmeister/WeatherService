package com.weather.service.infrastructure.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCategory {

    VALIDATION_ERROR(HttpStatus.BAD_REQUEST),
    AUTHENTICATION_ERROR(HttpStatus.UNAUTHORIZED),
    AUTHORIZATION_ERROR(HttpStatus.FORBIDDEN),
    CONFLICT_ERROR(HttpStatus.CONFLICT),
    NOT_FOUND(HttpStatus.NOT_FOUND),
    SYSTEM_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST);

    private final HttpStatus status;

    ErrorCategory(HttpStatus status) {
        this.status = status;
    }
}
