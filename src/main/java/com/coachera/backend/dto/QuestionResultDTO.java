package com.coachera.backend.dto;


import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "QuistionsResult Data Transfer Object")
public class QuestionResultDTO {
    @Schema(example = "1", description = "quistion ID")
    private Long questionId;
    
    @Schema(example = "true", description = "answer if correct or not")
    private boolean isCorrect;
     
    // private Integer submittedAnswerIndex;
    
    // private Integer correctAnswerIndex;

}
