package com.coachera.backend.generator;

import com.coachera.backend.entity.Question;
import com.coachera.backend.entity.Quiz;
import org.instancio.Instancio;
import org.instancio.Select;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class QuestionGenerator {

    private static final Random random = new Random();
    private static final String[] QUESTION_PREFIXES = {
        "What is", "Explain", "How does", "Why is", "When should", 
        "Which of these", "Identify the", "Select the correct"
    };

    private static final String[] QUESTION_SUBJECTS = {
        "main purpose", "key concept", "primary function", 
        "best approach", "correct interpretation", "most important factor"
    };

    public static List<Question> fromQuizzes(List<Quiz> quizzes) {
        if (quizzes == null || quizzes.isEmpty()) {
            throw new IllegalArgumentException("Quizzes list cannot be null or empty");
        }

        return quizzes.stream()
            .flatMap(quiz -> {
                // Ensure quiz is managed/persisted
                if (quiz.getId() == null) {
                    throw new IllegalStateException("Quiz must be persisted first (id cannot be null)");
                }

                int questionCount = random.nextInt(5) + 3; // 3-7 questions per quiz
                AtomicInteger questionNumber = new AtomicInteger(1);

                return Instancio.ofList(Question.class)
                    .size(questionCount)
                    .ignore(Select.field(Question::getId))
                    .supply(Select.field(Question::getQuiz), () -> quiz)
                    .supply(Select.field(Question::getContent), () -> 
                        generateQuestionText(questionNumber.getAndIncrement()))
                    .supply(Select.field(Question::getAnswer1), () -> generateAnswer(1))
                    .supply(Select.field(Question::getAnswer2), () -> generateAnswer(2))
                    .supply(Select.field(Question::getAnswer3), () -> random.nextBoolean() ? generateAnswer(3) : null)
                    .supply(Select.field(Question::getAnswer4), () -> random.nextBoolean() ? generateAnswer(4) : null)
                    .supply(Select.field(Question::getCorrectAnswerIndex), () -> random.nextInt(4) + 1)
                    .create()
                    .stream();
            })
            .collect(Collectors.toList());
    }

    private static String generateQuestionText(int number) {
        return String.format("%s the %s of this topic? (Question %d)",
            QUESTION_PREFIXES[random.nextInt(QUESTION_PREFIXES.length)],
            QUESTION_SUBJECTS[random.nextInt(QUESTION_SUBJECTS.length)],
            number
        );
    }

    private static String generateAnswer(int index) {
        String[] answerTypes = {
            "Correct answer",
            "Partially correct but incomplete",
            "Common misconception",
            "Completely incorrect",
            "Opposite of the truth",
            "Only true in specific cases"
        };
        return String.format("%s (Option %d)", answerTypes[random.nextInt(answerTypes.length)], index);
    }
}