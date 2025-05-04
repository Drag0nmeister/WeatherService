package com.weather.service.infrastructure.exception;

import jakarta.persistence.EntityExistsException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.webjars.NotFoundException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<ErrorResponse> handleInternalServerErrorException(final InternalServerErrorException ex) {
        return buildErrorResponse(ErrorCategory.SYSTEM_ERROR, ex);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(final ValidationException ex) {
        return buildErrorResponse(ErrorCategory.VALIDATION_ERROR, ex);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(final NotFoundException ex) {
        return buildErrorResponse(ErrorCategory.NOT_FOUND, ex);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(final ConstraintViolationException ex) {
        return buildErrorResponse(ErrorCategory.CONFLICT_ERROR, ex);
    }

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<ErrorResponse> handleEntityExistsException(final EntityExistsException ex) {
        return buildErrorResponse(ErrorCategory.CONFLICT_ERROR, ex);
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<ErrorResponse> handleEmptyResultDataAccessException(final EmptyResultDataAccessException ex) {
        return buildErrorResponse(ErrorCategory.NOT_FOUND, ex);
    }

    @ExceptionHandler(CityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCityNotFound(CityNotFoundException ex) {
        return buildErrorResponse(ErrorCategory.NOT_FOUND, ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(final Exception ex) {
        log.error("An unexpected exception occurred: {}", ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(ErrorCategory.SYSTEM_ERROR.getStatus().value())
                .developerMessage(ex.toString())
                .userMessage("Внутренняя ошибка сервиса")
                .build();
        return new ResponseEntity<>(errorResponse, ErrorCategory.SYSTEM_ERROR.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String firstErrorMessage = ex.getBindingResult().getAllErrors().stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse("Некорректный запрос");

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(status.value())
                .developerMessage(ex.toString())
                .userMessage(firstErrorMessage)
                .build();

        return new ResponseEntity<>(errorResponse, headers, status);
    }


    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String errorMessage = "Некорректный запрос";

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(status.value())
                .developerMessage(ex.getMessage())
                .userMessage(errorMessage)
                .build();
        return new ResponseEntity<>(errorResponse, headers, status);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(ErrorCategory errorCategory, Exception ex) {
        log.error("{}: {}", errorCategory.name(), ex.getMessage());
        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .errorCode(errorCategory.getStatus().value())
                        .developerMessage(ex.toString())
                        .userMessage(ex.getMessage())
                        .build(),
                errorCategory.getStatus());
    }
}
