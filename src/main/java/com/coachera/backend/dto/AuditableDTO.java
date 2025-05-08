package com.coachera.backend.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;


@MappedSuperclass
@Data
public abstract class AuditableDTO {
    @Schema(
        description = "Timestamp of when the entity was created",
        example = "2024-05-08T14:30:00",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    protected LocalDateTime createdAt;

    @Schema(
        description = "Timestamp of when the entity was last updated",
        example = "2024-05-08T15:45:00",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    protected LocalDateTime updatedAt;
}
