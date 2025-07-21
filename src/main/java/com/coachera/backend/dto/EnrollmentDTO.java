package com.coachera.backend.dto;

import java.util.Set;
import java.util.stream.Collectors;

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

    @Schema(required = true, description = "Info about course status")
    private CourseCompletionDTO courseCompletion;

    @Schema(required = true, description = "Info about course status")
    private Set<MaterialCompletionDTO> materialCompletions;

    // Constructor from entity
    public EnrollmentDTO(Enrollment enrollment) {
        this.id = enrollment.getId();
        this.courseId = enrollment.getCourse().getId();
        this.studentId = enrollment.getStudent().getId();
        if (enrollment.getCourseCompletion() != null) {
            this.courseCompletion = new CourseCompletionDTO(enrollment.getCourseCompletion());
        } else {
            this.courseCompletion = null;
        }

        this.materialCompletions = enrollment.getMaterialCompletions().stream().map(MaterialCompletionDTO::new)
                .collect(Collectors.toSet());
        this.setCreatedAt(enrollment.getCreatedAt());
        this.setUpdatedAt(enrollment.getUpdatedAt());
    }
}
