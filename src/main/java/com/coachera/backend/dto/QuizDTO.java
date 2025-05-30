package com.coachera.backend.dto;

import java.util.Set;
import java.util.stream.Collectors;

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
@Schema(description = "Quiz Data Transfer Object")
public class QuizDTO extends AuditableDTO {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "3")
    private Integer id;

    @Schema(required = true, example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer materialId;

    @Schema(description = "List of questions in the quiz")
    private Set<QuestionDTO> questions;

    public QuizDTO(Quiz quiz)
    {
        this.id = quiz.getId();
        this.materialId = quiz.getMaterial().getId();
        if (quiz.getQuestions() != null) {
            this.questions = quiz.getQuestions().stream()
                .map(QuestionDTO::new)
                .collect(Collectors.toSet());
        }
        this.setCreatedAt(quiz.getCreatedAt());
        this.setUpdatedAt(quiz.getUpdatedAt());
    }
}
