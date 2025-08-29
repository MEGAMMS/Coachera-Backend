package com.coachera.backend.controller;

import com.coachera.backend.dto.ApiResponse;
import com.coachera.backend.dto.QuizResultDTO;
import com.coachera.backend.dto.QuizSubmissionDTO;
import com.coachera.backend.entity.User;
import com.coachera.backend.service.QuizVerificationService;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quizzes")
public class QuizSubmissionController {

    private final QuizVerificationService quizVerificationService;

    public QuizSubmissionController(QuizVerificationService quizVerificationService) {
        this.quizVerificationService = quizVerificationService;
    }

    @PostMapping("/verify")
    public ApiResponse<?> verifyQuizAnswers(
        @RequestBody QuizSubmissionDTO request,
            @AuthenticationPrincipal User user) {
        QuizResultDTO result = quizVerificationService.verifyAnswers(request, user);

        return ApiResponse.success(result);
    }
}
