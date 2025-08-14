package com.coachera.backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "course_completion")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CourseCompletion extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "enrollment_id", insertable = false, updatable = false)
    private Enrollment enrollment;


    private BigDecimal progress;
    private boolean completed;
    private LocalDateTime completionDate;

    // @Enumerated(EnumType.STRING)
    // private CompletionCriteria criteriaMet;
}