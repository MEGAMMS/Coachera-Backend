package com.coachera.backend.controller;

import com.coachera.backend.dto.ApiResponse;
import com.coachera.backend.dto.QuizDTO;
import com.coachera.backend.dto.QuizResponseDTO;
import com.coachera.backend.entity.User;
import com.coachera.backend.service.QuizService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/quizzes")
public class QuizController {

    private final QuizService quizService;

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @PostMapping
    public ApiResponse<?> createQuiz(
            @Valid @RequestBody QuizDTO quizDTO,
            @AuthenticationPrincipal User user) {
        QuizResponseDTO createdQuiz = quizService.createQuiz(quizDTO, user);
        return ApiResponse.created("Quiz was created successfuly", createdQuiz);
    }

    @GetMapping("/{quizId}")
    public ApiResponse<?> getQuizById(
            @PathVariable Integer quizId) {
        QuizResponseDTO quizDTO = quizService.getQuizById(quizId);
        return ApiResponse.success(quizDTO);
    }

    @GetMapping("/materials/{materialId}")
    public ApiResponse<?> getAllQuizzesByMaterialId(
            @PathVariable Integer materialId) {
        List<QuizResponseDTO> quizzes = quizService.getAllQuizzesByMaterialId(materialId);
        return ApiResponse.success(quizzes);
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @DeleteMapping("/{quizId}")
    public ApiResponse<Void> deleteQuiz(
            @PathVariable Integer quizId,
            @AuthenticationPrincipal User user) {
        quizService.deleteQuiz(quizId, user);
        return ApiResponse.noContentResponse();
    }

}