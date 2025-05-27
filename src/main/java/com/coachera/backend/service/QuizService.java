package com.coachera.backend.service;

import com.coachera.backend.dto.QuizDTO;
import com.coachera.backend.entity.Material;
import com.coachera.backend.entity.Quiz;
import com.coachera.backend.exception.ResourceNotFoundException;
import com.coachera.backend.repository.MaterialRepository;
import com.coachera.backend.repository.QuizRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class QuizService {

    private final QuizRepository quizRepository;
    private final MaterialRepository materialRepository;

    public QuizService(QuizRepository quizRepository, MaterialRepository materialRepository) {
        this.quizRepository = quizRepository;
        this.materialRepository = materialRepository;
    }

    public QuizDTO createQuiz(Integer materialId, QuizDTO quizDTO) {
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("Material not found with id: " + materialId));

        Quiz quiz = new Quiz();
        quiz.setMaterial(material);

        Quiz savedQuiz = quizRepository.save(quiz);
        return new QuizDTO(savedQuiz);
    }

    @Transactional(readOnly = true)
    public QuizDTO getQuizById(Integer quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));
        return new QuizDTO(quiz);
    }

    @Transactional(readOnly = true)
    public List<QuizDTO> getAllQuizzesByMaterialId(Integer materialId) {
        if (!materialRepository.existsById(materialId)) {
            throw new ResourceNotFoundException("Material not found with id: " + materialId);
        }

        return quizRepository.findByMaterialId(materialId).stream()
                .map(QuizDTO::new)
                .collect(Collectors.toList());
    }

    public void deleteQuiz(Integer quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));
        quizRepository.delete(quiz);
    }
}