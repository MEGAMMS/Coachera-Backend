package com.coachera.backend.dto;

import java.util.*;
import java.util.stream.Collectors;
import com.coachera.backend.entity.LearningPath;
// import com.coachera.backend.dto.LearningPathCourseWithOrderDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class LearningPathWithCoursersDTO extends LearningPathDTO {
    private List<LearningPathCourseWithOrderDTO> courses;

    public LearningPathWithCoursersDTO(LearningPath learningPath) {
        super(learningPath);
        this.courses = learningPath.getCourses().stream()
            .sorted(Comparator.comparingInt(m -> m.getOrderIndex()))
            .map(LearningPathCourseWithOrderDTO::new)
            .collect(Collectors.toList());
    }

    public List<LearningPathCourseWithOrderDTO> getCoursesWithOrder() {
        return courses;
    }
    @JsonIgnore
    @Override
    public Set<LearningPathCourseDTO> getCourses() {
        return super.getCourses();
    }
}
