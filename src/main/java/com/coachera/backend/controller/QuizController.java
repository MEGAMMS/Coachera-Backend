package com.coachera.backend.controller;

import com.coachera.backend.dto.QuizDTO;
import com.coachera.backend.service.QuizService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/materials/{materialId}/quizzes")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping
    public ResponseEntity<QuizDTO> createQuiz(
            @PathVariable Integer materialId,
            @Valid @RequestBody QuizDTO quizDTO) {
        QuizDTO createdQuiz = quizService.createQuiz(materialId, quizDTO);
        return new ResponseEntity<>(createdQuiz, HttpStatus.CREATED);
    }

    @GetMapping("/{quizId}")
    public ResponseEntity<QuizDTO> getQuizById(
            @PathVariable Integer quizId) {
        QuizDTO quizDTO = quizService.getQuizById(quizId);
        return ResponseEntity.ok(quizDTO);
    }

    @GetMapping
    public ResponseEntity<List<QuizDTO>> getAllQuizzesByMaterialId(
            @PathVariable Integer materialId) {
        List<QuizDTO> quizzes = quizService.getAllQuizzesByMaterialId(materialId);
        return ResponseEntity.ok(quizzes);
    }

    @DeleteMapping("/{quizId}")
    public ResponseEntity<Void> deleteQuiz(
            @PathVariable Integer quizId) {
        quizService.deleteQuiz(quizId);
        return ResponseEntity.noContent().build();
    }
}