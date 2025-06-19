package com.coachera.backend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coachera.backend.dto.*;
import com.coachera.backend.entity.Enrollment;
import com.coachera.backend.entity.Material;
import com.coachera.backend.entity.MaterialCompletion;
import com.coachera.backend.entity.Question;
import com.coachera.backend.entity.Quiz;
import com.coachera.backend.entity.User;
import com.coachera.backend.entity.enums.CompletionState;
import com.coachera.backend.entity.enums.CompletionTriggerType;
import com.coachera.backend.exception.ResourceNotFoundException;
import com.coachera.backend.repository.EnrollmentRepository;
import com.coachera.backend.repository.MaterialCompletionRepository;
import com.coachera.backend.repository.MaterialRepository;
import com.coachera.backend.repository.QuizRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuizVerificationService {

    private final QuizRepository quizRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final MaterialRepository materialRepository;
    private final MaterialCompletionRepository materialCompletionRepository;

    @Transactional(readOnly = true)
    public QuizResultDTO verifyAnswers(QuizSubmissionDTO request, User user) {
        Quiz quiz = quizRepository.findById(request.getQuizId().intValue())
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found"));

        Set<Question> quizQuestions = quiz.getQuestions();
        Map<Long, Question> questionMap = quizQuestions.stream()
                .collect(Collectors.toMap(q -> q.getId().longValue(), q -> q));

        int correctCount = 0;
        List<QuestionResultDTO> results = new ArrayList<>();

        for (QuestionSubmissionDTO answer : request.getQuestions()) {
            Question question = questionMap.get(answer.getQuestionId());

            if (question == null) {
                throw new IllegalArgumentException(
                        "Question " + answer.getQuestionId() + " does not belong to Quiz " + quiz.getId());
            }

            boolean isCorrect = question.getCorrectAnswerIndex() == answer.getAnswerIndex();
            if (isCorrect) {
                correctCount++;
            }

            QuestionResultDTO result = new QuestionResultDTO();
            result.setQuestionId(question.getId().longValue());
            result.setCorrect(isCorrect);
            results.add(result);
        }

        int total = quizQuestions.size();
        double percentage = total == 0 ? 0 : (correctCount * 100.0 / total);

        // Storing score
        Integer studentId = user.getStudent().getId();
        Integer materialId = quiz.getMaterial().getId();
        Integer courseId = quiz.getMaterial().getSection().getModule().getCourse().getId();
        markMaterialComplete(studentId, courseId, materialId, percentage);

        QuizResultDTO response = new QuizResultDTO();
        response.setQuizId(request.getQuizId());
        response.setTotalQuestions(total);
        response.setCorrectAnswers(correctCount);
        response.setScorePercentage(percentage);
        response.setQuestionResults(results);

        return response;
    }

    public boolean isQuizPassed(Enrollment enrollment, Material material) {
        Optional<MaterialCompletion> existingCompletion = materialCompletionRepository
                    .findByEnrollmentAndMaterial(enrollment, material);
        if (existingCompletion.isPresent()){
            MaterialCompletion completion = existingCompletion.get();
            if(completion.getCompletionState()==CompletionState.COMPLETE_PASS)
            {
                return true;
            }
        }
        return false;
    }

    @Transactional
    public void markMaterialComplete(Integer studentId, Integer courseId, Integer materialId, double percentage) {
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Enrollment not found for student: " + studentId + " in course " + courseId));

        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("Material not found with id: " + materialId));

        MaterialCompletion completion = new MaterialCompletion();
        completion.setEnrollment(enrollment);
        completion.setMaterial(material);
        completion.setCompletionDate(LocalDateTime.now());
        completion.setTriggerType(CompletionTriggerType.GRADE);

        if (percentage > 70) {
            completion.setCompleted(true);
            completion.setCompletionState(CompletionState.COMPLETE_PASS);
        } else {
            completion.setCompleted(false);
            completion.setCompletionState(CompletionState.COMPLETE_FAIL);
        }

        materialCompletionRepository.save(completion);
    }
}
