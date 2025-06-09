package com.coachera.backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "course_completion")
public class CourseCompletion {
    @Id @ManyToOne
    private Enrollment enrollment;
    
    private BigDecimal progress;
    private boolean completed;
    private LocalDateTime completionDate;
    
    // @Enumerated(EnumType.STRING)
    // private CompletionCriteria criteriaMet;
}