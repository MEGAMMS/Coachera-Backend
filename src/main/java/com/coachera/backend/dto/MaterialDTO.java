package com.coachera.backend.dto;

import com.coachera.backend.entity.Material;
import com.coachera.backend.entity.Material.MaterialType;
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

    @Schema(required = true, example = "1st Material")
    private String title;

    @Schema(required = true, example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer sectionId;

    @Schema(required = true, example = "1")
    private Integer orderIndex;

    @Schema(required = true, example = "VIDEO", description = "Type of material (VIDEO, ARTICLE, QUIZ, PDF, etc.)")
    private MaterialType type;

    @Schema(description = "URL for video content", example = "https://example.com/video.mp4")
    private String videoUrl;

    @Schema(description = "Article content in text format", example = "This is a comprehensive article about...")
    private String article;

    @Schema(description = "Quiz attached to this material")
    private QuizDTO quiz;    

    public MaterialDTO(Material material) {
        this.id = material.getId();
        this.title = material.getTitle();
        this.sectionId = material.getSection().getId();
        this.orderIndex = material.getOrderIndex();
        this.type = material.getType();
        this.videoUrl = material.getVideoUrl();
        this.article = material.getArticle();

        Quiz quizEntity = material.getQuiz();
        if (quizEntity != null) {
            this.quiz = new QuizDTO(quizEntity);
        }

        this.setCreatedAt(material.getCreatedAt());
        this.setUpdatedAt(material.getUpdatedAt());
    }
}