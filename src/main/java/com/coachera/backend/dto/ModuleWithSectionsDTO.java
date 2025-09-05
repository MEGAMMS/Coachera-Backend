package com.coachera.backend.dto;

import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

import com.coachera.backend.entity.Module;
import com.coachera.backend.entity.Section;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Module DTO including full section details")
public class ModuleWithSectionsDTO extends ModuleDTO {

    @Schema(description = "Sections belonging to this module")
    private Set<SectionWithMaterialsDTO> sections;

    @Override
    @JsonIgnore
    public Set<Integer> getSectionIds() {
        return super.getSectionIds();
    }

    public ModuleWithSectionsDTO(Module module) {
        super(module);
        this.sections = module.getSections().stream()
                .sorted(Comparator.comparingInt(Section::getOrderIndex))
                .map(SectionWithMaterialsDTO::new)
                .collect(Collectors.toSet());
    }
}
