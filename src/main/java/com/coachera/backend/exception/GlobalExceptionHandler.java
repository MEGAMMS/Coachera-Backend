package com.coachera.backend.exception;

import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.coachera.backend.dto.ApiResponse;

import org.springframework.validation.FieldError;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ HttpMessageNotReadableException.class, BadRequestException.class })
    public ApiResponse<?> handleBadRequestExceptions(Exception ex) {
        String errorMessage = "Bad Request: " + ex.getMessage();
        return ApiResponse.error(HttpStatus.BAD_REQUEST, errorMessage);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        return ApiResponse.error(HttpStatus.BAD_REQUEST, "Validation Error: " + errorMessage);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ApiResponse<?> handleAutheriztionExceptions(AuthorizationDeniedException ex) {
        return ApiResponse.error(HttpStatus.UNAUTHORIZED, "Forbidden: " + ex.getMessage());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ApiResponse<?> handleNoResourceFoundExceptions(NoResourceFoundException ex) {
        return ApiResponse.error(HttpStatus.NOT_FOUND, "Not Found: " + ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<?> handleAllExceptions(Exception ex) {
        return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Error: " + ex.getMessage());
    }
}
