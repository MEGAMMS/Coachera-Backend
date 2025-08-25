package com.coachera.backend.dto;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Schema(example = "10")
    private String durationHours;

    @Schema(example = "999.12")
    private BigDecimal price;

    @Schema(example = "3.2")
    private BigDecimal rating;

    @Schema(description = "Whether the course is published and visible to the public", example = "false")
    private boolean isPublished;

    @Schema(description = "Associated org information", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer orgId;

    @Schema(example = "[2,3]", description = "Course's categories")
    private Set<CategoryDTO> categories;

    @Schema(example = "[2,3]", description = "Learning paths associated with this course")
    private Set<Integer> learningPathIds;

    @Schema(example = "[1,2,3]", description = "IDs of modules belonging to this course")
    private Set<Integer> moduleIds;

    @Schema(example = "[2,3]", description = "Learning paths associated with this course")
    private Set<Integer> instructors;

    @Schema(description = "Supporting image")
    private String image;

    public CourseDTO(Course course) {
        this.id = course.getId();
        this.title = course.getTitle();
        this.description = course.getDescription();
        this.orgId = course.getOrg().getId();
        this.durationHours = course.getDurationHours();
        this.price = course.getPrice();
        this.rating = course.getRating();
        this.instructors = course.getInstructors().stream().map(in -> in.getInstructor().getId())
        .collect(Collectors.toSet());
        this.categories = course.getCategories().stream().map(c -> new CategoryDTO(c.getCategory()))
                .collect(Collectors.toSet());
        this.learningPathIds = course.getLearningPaths().stream().map(lp -> lp.getLearningPath().getId())
                .collect(Collectors.toSet());
        this.moduleIds = course.getModules().stream().map(module -> module.getId())
                .collect(Collectors.toSet());
        if (course.getImage() != null) {
            this.image = course.getImage().getUrl();
        }
        this.isPublished = course.getIsPublished();
        this.setCreatedAt(course.getCreatedAt());
        this.setUpdatedAt(course.getUpdatedAt());
    }

    
}
