package com.coachera.backend.dto;

import com.coachera.backend.entity.Section;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Section Data Transfer Object")
public class SectionDTO extends AuditableDTO {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "3")
    private Integer id;

    @Schema(required = true, example = "1st Section")
    private String title;

    @Schema(required = true, example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer weekId;

    @Schema(required = true, example = "1")
    private Integer orderIndex;

    public SectionDTO(Section section) {
        this.id = section.getId();
        this.title = section.getTitle();
        this.weekId = section.getWeek().getId();
        this.orderIndex = section.getOrderIndex();
        this.setCreatedAt(section.getCreatedAt());
        this.setUpdatedAt(section.getUpdatedAt());
    }
}
