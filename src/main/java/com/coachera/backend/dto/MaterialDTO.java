package com.coachera.backend.dto;

import com.coachera.backend.entity.Material;
import com.coachera.backend.entity.Quiz;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Material Data Transfer Object")
public class MaterialDTO extends AuditableDTO {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "3")
    private Integer id;

    @Schema(required = true, example = "1st Materiak")
    private String title;

    @Schema(required = true, example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer sectionId;

    @Schema(required = true, example = "1")
    private Integer orderIndex;

    @Schema(description = "Quiz attached to this material")
    private QuizDTO quiz;    

    public MaterialDTO(Material material) {
        this.id = material.getId();
        this.title = material.getTitle();
        this.sectionId = material.getSection().getId();
        this.orderIndex = material.getOrderIndex();

        Quiz quizEntity = material.getQuiz();
        if (quizEntity != null) {
            this.quiz = new QuizDTO(quizEntity);
        }

        this.setCreatedAt(material.getCreatedAt());
        this.setUpdatedAt(material.getUpdatedAt());
    }
}
