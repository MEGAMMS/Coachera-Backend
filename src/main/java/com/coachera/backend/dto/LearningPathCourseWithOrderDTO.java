package com.coachera.backend.dto;

import com.coachera.backend.entity.LearningPathCourse;

public class LearningPathCourseWithOrderDTO {
    private CourseDTO course;
    private int orderIndex;

    public LearningPathCourseWithOrderDTO(LearningPathCourse lpc) {
        this.course = new CourseDTO(lpc.getCourse());
        this.orderIndex = lpc.getOrderIndex();
    }

    public CourseDTO getCourse() { return course; }
    public int getOrderIndex() { return orderIndex; }
}
