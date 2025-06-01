package com.coachera.backend.service;

import com.coachera.backend.dto.QuestionDTO;
import com.coachera.backend.dto.QuestionResponseDTO;
import com.coachera.backend.entity.Question;
import com.coachera.backend.entity.Quiz;
import com.coachera.backend.exception.InvalidQuestionException;
import com.coachera.backend.exception.ResourceNotFoundException;
import com.coachera.backend.repository.QuestionRepository;
import com.coachera.backend.repository.QuizRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;

    public QuestionService(QuestionRepository questionRepository, QuizRepository quizRepository) {
        this.questionRepository = questionRepository;
        this.quizRepository = quizRepository;
    }

    public QuestionDTO createQuestion(Integer quizId, QuestionDTO questionDTO) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));

        validateQuestion(questionDTO);

        Question question = new Question();
        question.setQuiz(quiz);
        question.setContent(questionDTO.getContent());
        question.setAnswer1(questionDTO.getAnswer1());
        question.setAnswer2(questionDTO.getAnswer2());
        question.setAnswer3(questionDTO.getAnswer3());
        question.setAnswer4(questionDTO.getAnswer4());
        question.setCorrectAnswerIndex(questionDTO.getCorrectAnswerIndex());

        Question savedQuestion = questionRepository.save(question);
        return new QuestionDTO(savedQuestion);
    }

    public QuestionDTO updateQuestion(Integer questionId, QuestionDTO questionDTO) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));

        validateQuestion(questionDTO);

        question.setContent(questionDTO.getContent());
        question.setAnswer1(questionDTO.getAnswer1());
        question.setAnswer2(questionDTO.getAnswer2());
        question.setAnswer3(questionDTO.getAnswer3());
        question.setAnswer4(questionDTO.getAnswer4());
        question.setCorrectAnswerIndex(questionDTO.getCorrectAnswerIndex());

        Question updatedQuestion = questionRepository.save(question);
        return new QuestionDTO(updatedQuestion);
    }

    @Transactional(readOnly = true)
    public QuestionResponseDTO getQuestionById(Integer questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));
        return new QuestionResponseDTO(question);
    }

    @Transactional(readOnly = true)
    public List<QuestionResponseDTO> getAllQuestionsByQuizId(Integer quizId) {
        if (!quizRepository.existsById(quizId)) {
            throw new ResourceNotFoundException("Quiz not found with id: " + quizId);
        }

        return questionRepository.findByQuizIdOrderByIdAsc(quizId).stream()
                .map(QuestionResponseDTO::new)
                .collect(Collectors.toList());
    }

    public void deleteQuestion(Integer questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));
        questionRepository.delete(question);
    }

    private void validateQuestion(QuestionDTO questionDTO) {
       
        if (questionDTO.getContent() == null || questionDTO.getContent().trim().isEmpty()) {
            throw new InvalidQuestionException("Question content cannot be empty");
        }

        if (questionDTO.getAnswer1() == null || questionDTO.getAnswer1().trim().isEmpty() ||
            questionDTO.getAnswer2() == null || questionDTO.getAnswer2().trim().isEmpty()) {
            throw new InvalidQuestionException("At least two answers are required");
        }

        // Validate correct answer index
        if (questionDTO.getCorrectAnswerIndex() == null || 
            questionDTO.getCorrectAnswerIndex() < 1 || 
            questionDTO.getCorrectAnswerIndex() > 4) {
            throw new InvalidQuestionException("Correct answer index must be between 1 and 4");
        }

        // Check if the selected correct answer exists
        String correctAnswer = switch (questionDTO.getCorrectAnswerIndex()) {
            case 1 -> questionDTO.getAnswer1();
            case 2 -> questionDTO.getAnswer2();
            case 3 -> questionDTO.getAnswer3();
            case 4 -> questionDTO.getAnswer4();
            default -> null;
        };

        if (correctAnswer == null || correctAnswer.trim().isEmpty()) {
            throw new InvalidQuestionException("Selected correct answer does not exist");
        }
    }
}