package com.coachera.backend.dto;

import lombok.Data;
import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
public class ApiResponse<T> extends ResponseEntity<Object> {
    // Create a response body structure
    @Data
    public static class ResponseBody<D> {
        @Schema(example = "200")
        private final int status;
        @Schema(example = "hello world")
        private final String message;
        private final D data;
        private final Instant timestamp = Instant.now();
    }

    public ApiResponse(HttpStatus status, String message, T data) {
        super(
                new ResponseBody<>(status.value(), message, data),
                status);
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

    public static ApiResponse<Void> noContentResponse() {
        return new ApiResponse<>(HttpStatus.NO_CONTENT, "No Content", null);
    }

    public static <T> ApiResponse<T> error(HttpStatus status, String message) {
        return new ApiResponse<>(status, message, null);
    }
}
