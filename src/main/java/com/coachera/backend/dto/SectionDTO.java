package com.coachera.backend.dto;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.coachera.backend.entity.Material;
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
    private Integer moduleId;

    @Schema(required = true, example = "1")
    private Integer orderIndex;

    @Schema(description = "List of materials in this section")
    private List<Integer> materials;

    public SectionDTO(Section section) {
        this.id = section.getId();
        this.title = section.getTitle();
        this.moduleId = section.getModule().getId();
        this.orderIndex = section.getOrderIndex();
        this.materials = section.getMaterials().stream()
                .sorted(Comparator.comparingInt(Material::getOrderIndex)) 
                .map(material -> material.getId()) 
                .collect(Collectors.toList());
        this.setCreatedAt(section.getCreatedAt());
        this.setUpdatedAt(section.getUpdatedAt());
    }
}
