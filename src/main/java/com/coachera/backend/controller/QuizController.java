package com.coachera.backend.controller;

import com.coachera.backend.dto.ApiResponse;
import com.coachera.backend.dto.QuizDTO;
import com.coachera.backend.service.QuizService;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/quizzes")
public class QuizController {

    private final QuizService quizService;

    @PostMapping
    public ApiResponse<QuizDTO> createQuiz(
            @PathVariable Integer materialId,
            @Valid @RequestBody QuizDTO quizDTO) {
        QuizDTO createdQuiz = quizService.createQuiz(materialId, quizDTO);
        return ApiResponse.created("Quiz was created successfuly",createdQuiz);
    }

    @GetMapping("/{quizId}")
    public ApiResponse<QuizDTO> getQuizById(
            @PathVariable Integer quizId) {
        QuizDTO quizDTO = quizService.getQuizById(quizId);
        return ApiResponse.success(quizDTO);
    }

    @GetMapping
    public ApiResponse<List<QuizDTO>> getAllQuizzesByMaterialId(
            @PathVariable Integer materialId) {
        List<QuizDTO> quizzes = quizService.getAllQuizzesByMaterialId(materialId);
        return ApiResponse.success(quizzes);
    }

    @DeleteMapping("/{quizId}")
    public ApiResponse<Void> deleteQuiz(
            @PathVariable Integer quizId) {
        quizService.deleteQuiz(quizId);
        return ApiResponse.noContentResponse();
    }

    
}