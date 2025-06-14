package com.coachera.backend.dto;

import com.coachera.backend.entity.CourseCompletion;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Course Completion Data Transfer Object")
public class CourseCompletionDTO extends AuditableDTO {
    @Schema(required = true, example = "123", description = "Enrollment ID associated with the course completion")
    private Integer enrollmentId;

    @Schema(required = true, example = "75.50", description = "Completion progress percentage (0-100)")
    private BigDecimal progress;

    @Schema(required = true, example = "false", description = "Whether the course is fully completed")
    private boolean completed;

    @Schema(example = "2024-05-15T10:30:00", description = "Date and time when the course was completed (if applicable)")
    private LocalDateTime completionDate;

    public CourseCompletionDTO(CourseCompletion courseCompletion) {
        this.enrollmentId = courseCompletion.getEnrollment().getId();
        this.progress = courseCompletion.getProgress();
        this.completed = courseCompletion.isCompleted();
        this.completionDate = courseCompletion.getCompletionDate();
        this.setCreatedAt(courseCompletion.getCreatedAt());
        this.setUpdatedAt(courseCompletion.getUpdatedAt());
    }
}