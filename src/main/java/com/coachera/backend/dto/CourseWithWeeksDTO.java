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
@Schema(description = "Course DTO including full week details")
public class CourseWithWeeksDTO extends CourseDTO {

    @Schema(description = "List of weeks in this course")
    private List<WeekDTO> weeks;
    
    @Override
    @JsonIgnore
    public Set<Integer> getWeekIds() {
        return super.getWeekIds();
    }


    public CourseWithWeeksDTO(Course course) {
        super(course);
        this.weeks = course.getWeeks().stream()
                .map(WeekDTO::new)
                .collect(Collectors.toList());
    }
}
