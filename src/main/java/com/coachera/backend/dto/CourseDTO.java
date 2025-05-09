package com.coachera.backend.dto;

import com.coachera.backend.entity.Course;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Course Data Transfer Object")
public class CourseDTO extends AuditableDTO {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "3")
    private Integer id;

    @Schema(example = "Java totorial")
    private String title;

    @Schema(example = "hello guys and welcome back.....")
    private String description;

    @Schema(description = "Associated org information")
    private OrganizationDTO org;

    public CourseDTO(Course course) {
        this.id = course.getId();
        this.title = course.getTitle();
        this.description = course.getDescription();
        this.org = new OrganizationDTO(course.getOrg()); // assumes Course has getOrg()
        this.setCreatedAt(course.getCreatedAt());
        this.setUpdatedAt(course.getUpdatedAt());
    }
}
