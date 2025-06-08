package com.coachera.backend.dto;


import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "QuizResult Data Transfer Object")
public class QuizResultDTO {
    @Schema(example = "1", description = "quiz ID")
    private Long quizId;

    @Schema(example = "5", description = "quistions numbre in quiz") 
    private Integer totalQuestions;

    @Schema(example = "3", description = "numbre of correct answers in quiz")
    private Integer correctAnswers;

    @Schema(example = "70.0", description = "the score percentage of correct answers")
    private Double scorePercentage;

    @Schema(description = "Questions result (the answers questions id and is correct or not)")
    private List<QuestionResultDTO> questionResults;

}
