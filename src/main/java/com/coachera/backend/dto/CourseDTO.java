package com.coachera.backend.dto;

import java.math.BigDecimal;

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

    @Schema(description = "Associated org information" , accessMode = Schema.AccessMode.READ_ONLY)
    private Integer orgId;



    public CourseDTO(Course course) {
        this.id = course.getId();
        this.title = course.getTitle();
        this.description = course.getDescription();
        this.orgId = course.getOrg().getId(); // assumes Course has getOrg()
        this.durationHours=course.getDurationHours();
        this.price =course.getPrice();
        this.rating=course.getRating();
        this.setCreatedAt(course.getCreatedAt());
        this.setUpdatedAt(course.getUpdatedAt());
    }
}
