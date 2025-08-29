package com.coachera.backend.dto;

import java.util.Collections;
import java.util.Optional;
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
@Schema(description = "Enrolled Course Data Transfer Object")
public class EnrolledCourseDTO {
    
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "1", description = "Unique identifier of the enrollment")
    private Integer enrollmentId;

    @Schema(required = true, description = "Enrolled course")
    private CourseDTO course;

    @Schema(required = true, description = "Info about course status")
    private CourseCompletionDTO courseCompletion;

    @Schema(required = true, description = "Info about course status")
    private Set<MaterialCompletionDTO> materialCompletions;

    // Constructor from entity
    public EnrolledCourseDTO(Enrollment enrollment) {
        this.enrollmentId= enrollment.getId();
        this.course = new CourseDTO(enrollment.getCourse());
        
        if (enrollment.getCourseCompletion() != null) {
            this.courseCompletion = new CourseCompletionDTO(enrollment.getCourseCompletion());
        } else {
            this.courseCompletion = null;
        }

        this.materialCompletions = Optional.ofNullable(enrollment.getMaterialCompletions())
        .orElse(Collections.emptySet())
        .stream()
        .map(MaterialCompletionDTO::new)
        .collect(Collectors.toSet());
    }
}
