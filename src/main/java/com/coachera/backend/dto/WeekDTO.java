package com.coachera.backend.dto;

import com.coachera.backend.entity.Week;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Week Data Transfer Object")
public class WeekDTO extends AuditableDTO {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "3")
    private Integer id;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "3")
    private Integer courseId;

    @Schema(required = true, example = "1")
    private Integer orderIndex;

    public WeekDTO(Week week) {
        this.id = week.getId();
        this.courseId = week.getCourse().getId();
        this.orderIndex = week.getOrderIndex();
    }
}
