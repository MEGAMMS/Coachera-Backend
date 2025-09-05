package com.coachera.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "materials")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Material extends Auditable {
    public enum MaterialType {
        VIDEO, ARTICLE, QUIZ
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    @Column(nullable = false)
    private String title;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaterialType type;

    @OneToOne(mappedBy = "material", cascade = CascadeType.ALL, orphanRemoval = true)
    private Quiz quiz;

    @OneToOne(mappedBy = "material", cascade = CascadeType.ALL, orphanRemoval = true)
    private Video video;

    @Column(columnDefinition = "TEXT",nullable = true)
    private String article;

    // Business logic methods
    public boolean isVideo() {
        return type == MaterialType.VIDEO;
    }

    public boolean isArticle() {
        return type == MaterialType.ARTICLE;
    }

    public boolean isQuiz() {
        return type == MaterialType.QUIZ;
    }
}