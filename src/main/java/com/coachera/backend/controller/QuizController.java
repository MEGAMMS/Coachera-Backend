package com.coachera.backend.controller;

import com.coachera.backend.dto.ApiResponse;
import com.coachera.backend.dto.QuizDTO;
import com.coachera.backend.dto.QuizResultDTO;
import com.coachera.backend.dto.QuizSubmissionDTO;
import com.coachera.backend.service.QuizService;
import com.coachera.backend.service.QuizVerificationService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/materials/{materialId}/quizzes")
public class QuizController {

    private final QuizService quizService;
    private final QuizVerificationService quizVerificationService;
    
    public QuizController(QuizService quizService, QuizVerificationService quizVerificationService) {
        this.quizService = quizService;
        this.quizVerificationService = quizVerificationService;
        
    }
   

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

    @PostMapping("/verify")
     public ApiResponse<Map<String, Object>> verifyQuizAnswers(@RequestBody QuizSubmissionDTO request) {
        QuizResultDTO result = quizVerificationService.verifyAnswers(request);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("data", result);

        return ApiResponse.success(responseMap);
    }
}