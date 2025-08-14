package com.coachera.backend.dto;

import java.util.Set;
import java.util.stream.Collectors;

import com.coachera.backend.entity.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Category Data Transfer Object")
public class CategoryDTO {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "1", description = "Unique identifier of the category")
    private Integer id;

    @Schema(required = true, example = "AI", description = "Name of the category")
    private String name;

    @Schema(example = "ai", description = "Icon name of the category")
    private String icon;

    // @Schema(example = "[2,3]", description = "Courses associated with this
    // category")
    // private Set<Integer> courseIds;

    // Constructor from entity
    public CategoryDTO(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.icon = category.getIcon();
        // this.courseIds = category.getCourses().stream().map(c ->
        // c.getCourse().getId()).collect(Collectors.toSet());
        // this.setCreatedAt(category.getCreatedAt());
        // this.setUpdatedAt(category.getUpdatedAt());
    }
}
