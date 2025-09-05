package com.coachera.backend.service;

import com.coachera.backend.dto.QuestionDTO;
import com.coachera.backend.dto.QuestionRequestDTO;
import com.coachera.backend.dto.QuestionResponseDTO;
import com.coachera.backend.entity.Course;
import com.coachera.backend.entity.Instructor;
import com.coachera.backend.entity.Question;
import com.coachera.backend.entity.Quiz;
import com.coachera.backend.entity.User;
import com.coachera.backend.exception.InvalidQuestionException;
import com.coachera.backend.exception.ResourceNotFoundException;
import com.coachera.backend.repository.InstructorRepository;
import com.coachera.backend.repository.QuestionRepository;
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
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private final InstructorRepository instructorRepository;

    public QuestionDTO createQuestion(Integer quizId, QuestionRequestDTO questionDTO, User user) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));

        validateQuestion(questionDTO);

        if (!isInstructorOfCourse(user, quiz.getMaterial().getSection().getModule().getCourse())) {
            throw new AccessDeniedException("You are not allowed to add a question to this course");
        }

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

    public QuestionDTO updateQuestion(Integer questionId, QuestionRequestDTO questionDTO, User user) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));

        validateQuestion(questionDTO);

        if (!isInstructorOfCourse(user, question.getQuiz().getMaterial().getSection().getModule().getCourse())) {
            throw new AccessDeniedException("You are not allowed to edit this question");
        }

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

    public void deleteQuestion(Integer questionId, User user) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));
        if (!isInstructorOfCourse(user, question.getQuiz().getMaterial().getSection().getModule().getCourse())) {
            throw new AccessDeniedException("You are not allowed to delete this question");
        }
        questionRepository.delete(question);
    }

    private void validateQuestion(QuestionRequestDTO questionDTO) {

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