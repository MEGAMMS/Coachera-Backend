package com.coachera.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating an Review.
 * Excludes IDs since they are managed by the system.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Review Request DTO (for create/update)")
public class ReviewRequestDTO {
    @Schema(example = "5", description = "Course ID being reviewed")
    private Integer courseId;

    @Schema(example = "4", description = "Rating (1-5)", minimum = "1", maximum = "5")
    private Integer rating;

    @Schema(example = "Great course with excellent content!", description = "Review comment")
    private String comment;
}
