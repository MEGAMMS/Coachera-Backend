package com.coachera.backend.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "quizzes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quiz extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "material_id", nullable = false, unique = true)
    private Material material;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Question> questions = new HashSet<>();

        // Helper method to add a question and maintain the bidirectional relationship
    public void addQuestion(Question question) {
        if (question != null) {
            if (questions == null) {
                questions = new HashSet<>();
            }
            question.setQuiz(this);
            questions.add(question);
        }
    }

    // Helper method to remove a question and maintain the bidirectional relationship
    public void deleteQuestion(Question question) {
        if (question != null && questions != null) {
            question.setQuiz(null);
            questions.remove(question);
        }
    }

    // Convenience method to remove a question by ID
    public void deleteQuestionById(Integer questionId) {
        if (questionId != null && questions != null) {
            questions.removeIf(q -> q.getId().equals(questionId));
        }
    }
}