package com.coachera.backend.dto;

import com.coachera.backend.entity.LearningPathCourse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Learning Path Course relationship DTO")
class LearningPathCourseDTO {
    @Schema(example = "1", description = "Course ID")
    private Integer courseId;

    @Schema(example = "1", description = "Order index in the learning path")
    private Integer orderIndex;

    public LearningPathCourseDTO(LearningPathCourse learningPathCourse) {
        this.courseId = learningPathCourse.getCourse().getId();
        this.orderIndex = learningPathCourse.getOrderIndex();
    }
}