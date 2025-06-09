package com.coachera.backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "material_completion")
public class MaterialCompletion {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    private Enrollment enrollment;
    
    @ManyToOne
    private Material material;
    
    private boolean completed;
    private LocalDateTime completionDate;
    private Integer completionState; // 0=incomplete, 1=complete, 2=complete via requirements
    
    @Enumerated(EnumType.STRING)
    private CompletionTriggerType triggerType; // MANUAL, AUTOMATIC, SYSTEM
}

enum CompletionTriggerType {
    MANUAL,         // User marked complete
    AUTOMATIC,      // System marked based on criteria
    VIEWING,        // Completed by viewing
    GRADE           // Completed by achieving grade
}