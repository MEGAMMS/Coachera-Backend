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
@Schema(description = "QuizSubmisions Data Transfer Object")
public class QuizSubmissionDTO extends AuditableDTO {
    
    @Schema(example = "1", description = "Quiz ID this question belongs to")
    private Long quizId;

    @Schema( description = "The answers of student submissions")
    private List<QuestionSubmissionDTO> questions;

  
}