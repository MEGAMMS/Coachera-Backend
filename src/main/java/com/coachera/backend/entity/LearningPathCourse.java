package com.coachera.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "learning_path_courses")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class LearningPathCourse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "learning_path_id")
    private LearningPath learningPath;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(name = "order_index")
    private Integer orderIndex;
}