package com.coachera.backend.dto;

import lombok.Data;
import java.time.Instant;

import org.springframework.http.HttpStatus;

@Data
public class ApiResponse<T> {
    private int status;
    private String message;
    private T data;
    private Instant timestamp = Instant.now();

    public ApiResponse(HttpStatus status, String message, T data) {
        this.status = status.value();
        this.message = message;
        this.data = data;
    }

    // Helper static methods for common responses
    public static <T> ApiResponse<T> created(String message, T data) {
        return new ApiResponse<>(HttpStatus.CREATED, message, data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(HttpStatus.OK, message, data);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(HttpStatus.OK, "Success", data);
    }

    public static ApiResponse<Void> noContent() {
        return new ApiResponse<>(HttpStatus.NO_CONTENT, "No Content", null);
    }

    public static <T> ApiResponse<T> error(HttpStatus status, String message) {
        return new ApiResponse<>(status, message, null);
    }
}
