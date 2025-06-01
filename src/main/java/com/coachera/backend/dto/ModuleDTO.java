package com.coachera.backend.dto;

import java.util.Set;
import java.util.stream.Collectors;

import com.coachera.backend.entity.Module;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Module Data Transfer Object")
public class ModuleDTO extends AuditableDTO {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "3")
    private Integer id;

    @Schema(required = true, example = "Introduction to Meg3mizm")
    private String title;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "3")
    private Integer courseId;

    @Schema(required = true, example = "1")
    private Integer orderIndex;

    @Schema(description = "Sections belonging to this module")
    private Set<SectionDTO> sections;

    public ModuleDTO(Module module) {
        this.id = module.getId();
        this.courseId = module.getCourse().getId();
        this.orderIndex = module.getOrderIndex();
        this.title= module.getTitle();
        this.sections = module.getSections().stream()
                .map(SectionDTO::new)
                .collect(Collectors.toSet());
    }
}
