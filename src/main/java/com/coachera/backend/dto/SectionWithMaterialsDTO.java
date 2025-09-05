package com.coachera.backend.dto;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.coachera.backend.entity.Material;
import com.coachera.backend.entity.Section;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Section DTO including full material details")
public class SectionWithMaterialsDTO extends SectionDTO {

    @Schema(description = "Materials belonging to this section")
    private List<MaterialDTO> materials;

    @Override
    @JsonIgnore
    public List<Integer> getMaterialIds() {
        return super.getMaterialIds();
    }

    public SectionWithMaterialsDTO(Section section) {
        super(section);
        this.materials = section.getMaterials().stream()
                .sorted(Comparator.comparingInt(Material::getOrderIndex))
                .map(MaterialDTO::new)
                .collect(Collectors.toList());
    }
}
