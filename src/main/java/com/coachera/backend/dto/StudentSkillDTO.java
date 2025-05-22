package com.coachera.backend.dto;

import com.coachera.backend.entity.StudentSkill;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Student Skill relationship DTO")
public class StudentSkillDTO {
    @Schema(example = "1", description = "Skill ID")
    private Integer skillId;

    @Schema(example = "Java", description = "Skill name")
    private String skillName;

    @Schema(example = "1", description = "Course ID where skill was acquired")
    private Integer courseId;

    @Schema(example = "3", description = "Skill proficiency level (1-5)")
    private Integer level;

    public StudentSkillDTO(StudentSkill studentSkill) {
        this.skillId = studentSkill.getSkill().getId();
        this.skillName = studentSkill.getSkill().getName();
        this.courseId = studentSkill.getCourse() != null ? studentSkill.getCourse().getId() : null;
        this.level = studentSkill.getLevel();
        }
    }
