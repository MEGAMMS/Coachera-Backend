package com.coachera.backend.dto;

import com.coachera.backend.entity.Enrollment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Enrollment Data Transfer Object")
public class EnrollmentDTO extends AuditableDTO {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "1", description = "Unique identifier of the enrollment")
    private Integer id;

    @Schema(required = true, description = "Associated course")
    private Integer courseId;

    @Schema(required = true, description = "Associated student")
    private Integer studentId;

    @Schema(required = true, example = "75%", description = "Student's course progress")
    private String progress;

    // Constructor from entity
    public EnrollmentDTO(Enrollment enrollment) {
        this.id = enrollment.getId();
        this.courseId = enrollment.getCourse().getId();
        this.studentId = enrollment.getStudent().getId();
        this.progress = enrollment.getProgress();
        this.setCreatedAt(enrollment.getCreatedAt());
        this.setUpdatedAt(enrollment.getUpdatedAt());
    }
}
