package com.coachera.backend.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "learning_paths")
@Getter @Setter
@AllArgsConstructor
public class LearningPath {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "org_id", nullable = false)
    private Organization organization;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String image;

    
    @OneToMany(mappedBy = "learningPath", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    @Builder.Default
    private Set<LearningPathCourse> courses = new HashSet<>();

    // Helper method
    public void addCourse(Course course, Integer orderIndex) {
        LearningPathCourse learningPathCourse = new LearningPathCourse(this, course, orderIndex);
        courses.add(learningPathCourse);
        course.getLearningPaths().add(learningPathCourse);
    }

    public void removeCourse(Course course) {
        LearningPathCourse learningPathCourse = new LearningPathCourse(this, course);
        courses.remove(learningPathCourse);
        course.getLearningPaths().remove(learningPathCourse);
        learningPathCourse.setLearningPath(null);
        learningPathCourse.setCourse(null);
    }
}