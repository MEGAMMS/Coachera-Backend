package com.coachera.backend.dto;

import com.coachera.backend.entity.CourseInstructor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Course Instructor relationship DTO")
public class CourseInstructorDTO {
    @Schema(example = "1", description = "Instructor ID")
    private Integer instructorId;
    
    public CourseInstructorDTO(CourseInstructor courseInstructor) {
        this.instructorId = courseInstructor.getInstructor().getId();
    }
}