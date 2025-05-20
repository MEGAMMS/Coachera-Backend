package com.coachera.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "learning_path_courses")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningPathCourse extends Auditable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "learning_path_id", nullable = false)
    private LearningPath learningPath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    public LearningPathCourse(LearningPath learningPath, Course course, Integer orderIndex) {
        this.learningPath = learningPath;
        this.course = course;
        this.orderIndex = orderIndex;
    }

    public LearningPathCourse(LearningPath learningPath, Course course) {
        this.learningPath = learningPath;
        this.course = course;
    }

    // Equals and hashCode implementations to prevent duplicate entries
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LearningPathCourse)) return false;
        LearningPathCourse that = (LearningPathCourse) o;
        return learningPath != null && learningPath.getId() != null && 
               course != null && course.getId() != null &&
               learningPath.getId().equals(that.learningPath.getId()) && 
               course.getId().equals(that.course.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // Bidirectional relationship management methods
    public void setLearningPath(LearningPath learningPath) {
        if (this.learningPath != null) {
            this.learningPath.getCourses().remove(this);
        }
        this.learningPath = learningPath;
        if (learningPath != null) {
            learningPath.getCourses().add(this);
        }
    }

    public void setCourse(Course course) {
        if (this.course != null) {
            this.course.getLearningPaths().remove(this);
        }
        this.course = course;
        if (course != null) {
            course.getLearningPaths().add(this);
        }
    }
}