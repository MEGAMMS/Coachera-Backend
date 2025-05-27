package com.coachera.backend.dto;

import com.coachera.backend.entity.Course;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Course DTO including full module details")
public class CourseWithModulesDTO extends CourseDTO {

    @Schema(description = "List of modules in this course")
    private List<ModuleDTO> modules;
    
    @Override
    @JsonIgnore
    public Set<Integer> getModuleIds() {
        return super.getModuleIds();
    }


    public CourseWithModulesDTO(Course course) {
        super(course);
        this.modules = course.getModules().stream()
                .map(ModuleDTO::new)
                .collect(Collectors.toList());
    }
}
