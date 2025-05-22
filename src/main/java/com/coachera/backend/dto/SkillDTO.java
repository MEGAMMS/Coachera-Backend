package com.coachera.backend.dto;

import com.coachera.backend.entity.Skill;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Skill Data Transfer Object")
public class SkillDTO extends AuditableDTO {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "1")
    private Integer id;

    @Schema(example = "Java Programming", description = "Unique name of the skill")
    private String name;

    @Schema(description = "IDs of students who have this skill", accessMode = Schema.AccessMode.READ_ONLY)
    private Set<Integer> studentIds;

    public SkillDTO(Skill skill) {
        this.id = skill.getId();
        this.name = skill.getName();
        this.studentIds = skill.getStudentSkills().stream()
                .map(studentSkill -> studentSkill.getStudent().getId())
                .collect(Collectors.toSet());
        this.setCreatedAt(skill.getCreatedAt());
        this.setUpdatedAt(skill.getUpdatedAt());
    }
}