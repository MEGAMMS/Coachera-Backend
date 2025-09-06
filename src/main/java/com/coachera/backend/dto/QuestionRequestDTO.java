package com.coachera.backend.dto;

import com.coachera.backend.entity.Question;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Question Request Data Transfer Object (without correct answer)")
public class QuestionRequestDTO {
    @Schema(example = "1", description = "Question ID")
    private Integer id;
    
    @Schema(example = "What is the capital of France?", description = "Question content")
    private String content;

    @Schema(example = "Berlin", description = "First answer option")
    private String answer1;

    @Schema(example = "Madrid", description = "Second answer option")
    private String answer2;

    @Schema(example = "Paris", description = "Third answer option (optional)")
    private String answer3;

    @Schema(example = "Rome", description = "Fourth answer option (optional)")
    private String answer4;

    @Schema(example = "3", description = "Index of the correct answer (1-4)")
    private Integer correctAnswerIndex;

    public QuestionRequestDTO(Question question){
        this.id = question.getId();
        this.content = question.getContent();
        this.answer1 = question.getAnswer1();
        this.answer2 = question.getAnswer2();
        this.answer3 = question.getAnswer3();
        this.answer4 = question.getAnswer4();
        this.correctAnswerIndex =question.getCorrectAnswerIndex();
    }
}
