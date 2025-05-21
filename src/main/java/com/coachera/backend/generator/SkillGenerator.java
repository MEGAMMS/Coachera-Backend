package com.coachera.backend.generator;

import com.coachera.backend.entity.Skill;
import org.instancio.Instancio;
import org.instancio.Select;

import java.util.List;
import java.util.stream.Collectors;

public class SkillGenerator {
    public static List<Skill> generateSkills(List<String> skillNames) {
        return skillNames.stream()
                .map(skillName -> {
                    try {
                        Skill skill = Instancio.of(Skill.class)
                                .ignore(Select.field(Skill::getId))
                                .supply(Select.field(Skill::getName), () -> skillName)
                                .ignore(Select.field(Skill::getStudentSkills))
                                .create();

                        if (skill == null) {
                            throw new IllegalStateException("Instancio returned null Skill");
                        }
                        return skill;
                    } catch (Exception e) {
                        throw new IllegalStateException("Failed to create Skill with name " + skillName, e);
                    }
                })
                .collect(Collectors.toList());
    }
}