package com.coachera.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "questions")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "answer_1", columnDefinition = "TEXT", nullable = false)
    private String answer1;

    @Column(name = "answer_2", columnDefinition = "TEXT", nullable = false)
    private String answer2;

    @Column(name = "answer_3", columnDefinition = "TEXT")
    private String answer3;

    @Column(name = "answer_4", columnDefinition = "TEXT")
    private String answer4;

    @Column(name = "answer_index_correct", nullable = false)
    private Integer correctAnswerIndex;
}