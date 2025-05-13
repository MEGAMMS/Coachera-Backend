package com.coachera.backend.dto;

import lombok.Data;
import java.time.Instant;

@Data
public class ApiResponse<T> {
    private int status;
    private String message;
    private T data;
    private Instant timestamp;

    public ApiResponse(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.timestamp = Instant.now();
    }

    public ApiResponse(T data) {
        this.status = 200;
        this.message = "Success";
        this.data = data;
        this.timestamp = Instant.now();
    }
}
