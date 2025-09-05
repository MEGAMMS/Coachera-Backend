package com.coachera.backend.dto;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import java.util.Map;
import com.coachera.backend.entity.MaterialCompletion;
import java.math.RoundingMode;

import com.coachera.backend.entity.Enrollment;
import com.coachera.backend.entity.Material;

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

    @Schema(required = true, description = "progress")
    private BigDecimal progress;


    public static MaterialCompletionDTO notCompleted(Material material, Integer enrollmentId) {
    MaterialCompletionDTO dto = new MaterialCompletionDTO();
    dto.setMaterialId(material.getId());
    dto.setEnrollmentId(enrollmentId);
    dto.setCompleted(false);
    // set other fields as needed
    return dto;
}
    // Constructor from entity
    public EnrolledCourseDTO(Enrollment enrollment, Set<Material> allMaterials) {
        this.enrollmentId = enrollment.getId();
        this.course = new CourseDTO(enrollment.getCourse());
    
        if (enrollment.getCourseCompletion() != null) {
            this.courseCompletion = new CourseCompletionDTO(enrollment.getCourseCompletion());
        } else {
            this.courseCompletion = null;
        }
    
        // Map materialId to completion for quick lookup
        Map<Integer, MaterialCompletion> completionMap = Optional.ofNullable(enrollment.getMaterialCompletions())
            .orElse(Collections.emptySet())
            .stream()
            .collect(Collectors.toMap(mc -> mc.getMaterial().getId(), mc -> mc));
    
        // Build DTOs for all materials
        this.materialCompletions = allMaterials.stream()
            .map(material -> {
                MaterialCompletion mc = completionMap.get(material.getId());
                if (mc != null) {
                    return new MaterialCompletionDTO(mc);
                } else {
                    // Create a DTO for not completed material
                    return EnrolledCourseDTO.notCompleted(material, enrollment.getId());
                }
            })
            .collect(Collectors.toSet());
    
        // Calculate progress
        long completedCount = this.materialCompletions.stream().filter(MaterialCompletionDTO::isCompleted).count();
        this.progress = allMaterials.isEmpty() ? BigDecimal.ZERO :
            BigDecimal.valueOf(completedCount * 100.0 / allMaterials.size()).setScale(2, RoundingMode.HALF_UP);
    }
    
}
