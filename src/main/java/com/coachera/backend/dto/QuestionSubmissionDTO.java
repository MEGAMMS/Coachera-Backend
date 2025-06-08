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
@Schema(description = "Question Data Transfer Object")
public class QuestionSubmissionDTO {

   @Schema(example = "1", description = "quistion ID")
    private Long questionId;
    
    @Schema(example = "2", description = "Question user answer")
    private Integer answerIndex;

}
