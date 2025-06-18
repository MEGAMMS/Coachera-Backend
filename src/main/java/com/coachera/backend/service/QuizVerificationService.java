package com.coachera.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coachera.backend.dto.*;
import com.coachera.backend.entity.Enrollment;
import com.coachera.backend.entity.Material;
import com.coachera.backend.entity.Question;
import com.coachera.backend.entity.Quiz;
import com.coachera.backend.repository.QuizRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuizVerificationService {

    private final QuizRepository quizRepository;

    @Transactional(readOnly = true)
    public QuizResultDTO verifyAnswers(QuizSubmissionDTO request) {
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

        QuizResultDTO response = new QuizResultDTO();
        response.setQuizId(request.getQuizId());
        response.setTotalQuestions(total);
        response.setCorrectAnswers(correctCount);
        response.setScorePercentage(percentage);
        response.setQuestionResults(results);

        return response;
    }

    public boolean isQuizPassed(Enrollment enrollment, Material material) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isQuizPassed'");
    }
}
