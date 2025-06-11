package com.coachera.backend.entity;

import java.time.LocalDateTime;

import com.coachera.backend.entity.enums.CompletionState;
import com.coachera.backend.entity.enums.CompletionTriggerType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "material_completion")
@AllArgsConstructor @NoArgsConstructor @Getter @Setter
public class MaterialCompletion extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Enrollment enrollment;

    @ManyToOne
    private Material material;

    private boolean completed;
    private LocalDateTime completionDate;
    private CompletionState completionState; // 0=incomplete, 1=complete, 2=complete via requirements

    @Enumerated(EnumType.STRING)
    private CompletionTriggerType triggerType; // MANUAL, AUTOMATIC, SYSTEM
}
