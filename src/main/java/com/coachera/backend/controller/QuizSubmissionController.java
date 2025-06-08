package com.coachera.backend.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coachera.backend.dto.ApiResponse;
import com.coachera.backend.dto.QuizResultDTO;
import com.coachera.backend.dto.QuizSubmissionDTO;
import com.coachera.backend.service.QuizVerificationService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quizzes")
public class QuizSubmissionController {

    private final QuizVerificationService quizVerificationService ;

    public QuizSubmissionController(QuizVerificationService quizVerificationService) {
        this.quizVerificationService = quizVerificationService;
    }

    @PostMapping("/verify")
     public ApiResponse<Map<String, Object>> verifyQuizAnswers(@RequestBody QuizSubmissionDTO request) {
        QuizResultDTO result = quizVerificationService.verifyAnswers(request);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("data", result);

        return ApiResponse.success(responseMap);
    }
}
