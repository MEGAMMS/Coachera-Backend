package com.coachera.backend.dto;

import java.util.Set;
import java.util.stream.Collectors;

import com.coachera.backend.entity.LearningPath;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Learning Path Data Transfer Object")
public class LearningPathDTO extends AuditableDTO {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "1")
    private Integer id;

    @Schema(example = "1", description = "Organization ID")
    private Integer orgId;

    @Schema(example = "Java Learning Path")
    private String title;

    @Schema(example = "Comprehensive path to learn Java from basics to advanced")
    private String description;

    @Schema(example = "java-path.jpg", description = "Image URL or filename")
    private String image;

    @Schema(description = "Courses in this learning path with their order")
    private Set<LearningPathCourseDTO> courses;

    public LearningPathDTO(LearningPath learningPath) {
        this.id = learningPath.getId();
        this.orgId = learningPath.getOrganization().getId();
        this.title = learningPath.getTitle();
        this.description = learningPath.getDescription();
        this.image = learningPath.getImage().getUrl();
        this.courses = learningPath.getCourses().stream()
                .map(LearningPathCourseDTO::new)
                .collect(Collectors.toSet());
        this.setCreatedAt(learningPath.getCreatedAt());
        this.setUpdatedAt(learningPath.getUpdatedAt());
    }
}