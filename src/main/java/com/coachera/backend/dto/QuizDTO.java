package com.coachera.backend.dto;

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
public class QuizDTO {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "3")
    private Integer id;

    @Schema(required = true, example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer materialId;

    public QuizDTO(Quiz quiz)
    {
        this.id = quiz.getId();
        this.materialId = quiz.getMaterial().getId();
    }
}
