package com.coachera.backend.service;

import com.coachera.backend.dto.QuizDTO;
import com.coachera.backend.dto.QuizResponseDTO;
import com.coachera.backend.entity.Course;
import com.coachera.backend.entity.Instructor;
import com.coachera.backend.entity.Material;
import com.coachera.backend.entity.Quiz;
import com.coachera.backend.entity.User;
import com.coachera.backend.exception.ResourceNotFoundException;
import com.coachera.backend.repository.InstructorRepository;
import com.coachera.backend.repository.MaterialRepository;
import com.coachera.backend.repository.QuizRepository;
import com.coachera.backend.repository.UserRepository;

import lombok.AllArgsConstructor;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class QuizService {

    private final QuizRepository quizRepository;
    private final MaterialRepository materialRepository;
    private final UserRepository userRepository;
    private final InstructorRepository instructorRepository;

    private final QuestionService questionService;

    public QuizResponseDTO createQuiz(QuizDTO quizDTO, User user) {
        Material material = materialRepository.findById(quizDTO.getMaterialId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Material not found with id: " + quizDTO.getMaterialId()));

        if (!isInstructorOfCourse(user, material.getSection().getModule().getCourse())) {
            throw new AccessDeniedException("You are not allowed to create a quiz");
        }

        Quiz quiz = new Quiz();
        quiz.setMaterial(material);

        if (quizDTO.getQuestions() != null) {
            quizDTO.getQuestions()
                    .forEach(questionDTO -> questionService.createQuestion(quiz.getId(), questionDTO, user));
        }

        // Quiz savedQuiz = quizRepository.save(quiz);
        return new QuizResponseDTO(quiz);
    }

    @Transactional(readOnly = true)
    public QuizResponseDTO getQuizById(Integer quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));
        return new QuizResponseDTO(quiz);
    }

    @Transactional(readOnly = true)
    public List<QuizResponseDTO> getAllQuizzesByMaterialId(Integer materialId) {
        if (!materialRepository.existsById(materialId)) {
            throw new ResourceNotFoundException("Material not found with id: " + materialId);
        }

        return quizRepository.findByMaterialId(materialId).stream()
                .map(QuizResponseDTO::new)
                .collect(Collectors.toList());
    }

    public void deleteQuiz(Integer quizId, User user) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));

        if (!isInstructorOfCourse(user, quiz.getMaterial().getSection().getModule().getCourse())) {
            throw new AccessDeniedException("You are not allowed to delete this course");
        }

        quizRepository.delete(quiz);
    }

    // Helper method
    private boolean isInstructorOfCourse(User user, Course course) {
        if (!userRepository.findById(user.getId()).isPresent()) {
            throw new IllegalArgumentException("User not found");
        }
        if (!user.isInstructor()) {
            throw new IllegalArgumentException("Instructor not found");
        }

        Instructor instructor = instructorRepository.findById(user.getInstructor().getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Instructor not found with id: " + user.getInstructor().getId()));

        if (course.getInstructors() == null || course.getInstructors().isEmpty()) {
            return false;
        }
        return course.getInstructors().stream()
                .anyMatch(ci -> ci.getInstructor().equals(instructor));
    }
}