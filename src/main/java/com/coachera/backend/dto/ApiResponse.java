package com.coachera.backend.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.coachera.backend.dto.pagination.PaginatedResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@EqualsAndHashCode(callSuper = false)
public class ApiResponse<T> extends ResponseEntity<Object> {
    // Create a response body structure
    @Data
    public static class ResponseBody<D> {
        @Schema(example = "200")
        private final int status;
        @Schema(example = "Operation successful")
        private final String message;
        private final D data;
        @Schema(example = "2024-03-20T10:30:00Z")
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

    public static <T> ApiResponse<PaginatedResponse<T>> paginated(Page<T> page) {
        return new ApiResponse<>(
            HttpStatus.OK,
            "Successfully retrieved paginated data",
            PaginatedResponse.of(page)
        );
    }

    public static ApiResponse<Void> noContentResponse() {
        return new ApiResponse<>(HttpStatus.NO_CONTENT, "No Content", null);
    }

    public static <T> ApiResponse<T> error(HttpStatus status, String message) {
        return new ApiResponse<>(status, message, null);
    }
}
