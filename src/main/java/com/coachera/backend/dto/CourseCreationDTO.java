package com.coachera.backend.dto;

import java.math.BigDecimal;
import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Course Creation Request Data Transfer Object")
public class CourseCreationDTO {
    @Schema(example = "Java totorial")
    private String title;

    @Schema(example = "hello guys and welcome back.....")
    private String description;

    @Schema(example = "10")
    private String durationHours;

    @Schema(example = "999.12")
    private BigDecimal price;

    @Schema(example = "[2,3]", description = "Course's categories")
    private Set<CategoryDTO> categories;

    @Schema(example = "[2,3]", description = "Learning paths associated with this course")
    private Set<Integer> instructors;

    @Schema(example = "http://localhost:8080/images/383980c4-a679-4d8a-a23c-f00ffbf25d76.png", nullable = true)
    private String imageUrl;

}
