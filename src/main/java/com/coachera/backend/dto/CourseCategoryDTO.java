package com.coachera.backend.dto;

import com.coachera.backend.entity.CourseCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Course-Category Association Data Transfer Object")
public class CourseCategoryDTO extends AuditableDTO {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "1", description = "Unique identifier of the association")
    private Integer id;

    @Schema(required = true, description = "Associated course")
    private CourseDTO course;

    @Schema(required = true, description = "Associated category")
    private CategoryDTO category;

    // Constructor from entity
    public CourseCategoryDTO(CourseCategory courseCategory) {
        this.id = courseCategory.getId();
        this.course = new CourseDTO(courseCategory.getCourse());
        this.category = new CategoryDTO(courseCategory.getCategory());
        this.setCreatedAt(courseCategory.getCreatedAt());
        this.setUpdatedAt(courseCategory.getUpdatedAt());
    }
}
