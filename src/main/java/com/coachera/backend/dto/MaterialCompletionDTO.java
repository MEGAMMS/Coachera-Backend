package com.coachera.backend.dto;

import com.coachera.backend.entity.MaterialCompletion;
import com.coachera.backend.entity.enums.CompletionState;
import com.coachera.backend.entity.enums.CompletionTriggerType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Material Completion Data Transfer Object")
public class MaterialCompletionDTO extends AuditableDTO {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "3", description = "Unique identifier of the material completion record")
    private Long id;

    @Schema(required = true, example = "1", description = "Enrollment ID associated with this completion")
    private Integer enrollmentId;

    @Schema(required = true, example = "5", description = "Material ID that was completed")
    private Integer materialId;

    @Schema(required = true, example = "true", description = "Whether the material is marked as completed")
    private boolean completed;

    @Schema(required = true, example = "INCOMPLETE", description = "State of completion")
    private CompletionState completionState;

    @Schema(example = "2024-05-08T14:30:00", description = "Date and time when the material was completed")
    private LocalDateTime completionDate;

    @Schema(example = "USER", description = "Type of trigger that caused the completion")
    private CompletionTriggerType triggerType;

    public MaterialCompletionDTO(MaterialCompletion materialCompletion) {
        this.id = materialCompletion.getId();
        this.enrollmentId = materialCompletion.getEnrollment().getId();
        this.materialId = materialCompletion.getMaterial().getId();
        this.completed = materialCompletion.isCompleted();
        this.completionState = materialCompletion.getCompletionState();
        this.completionDate = materialCompletion.getCompletionDate();
        this.triggerType = materialCompletion.getTriggerType();
        this.setCreatedAt(materialCompletion.getCreatedAt());
        this.setUpdatedAt(materialCompletion.getUpdatedAt());
    }
}
