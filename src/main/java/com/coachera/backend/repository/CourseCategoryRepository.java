package com.coachera.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coachera.backend.entity.CourseCategory;

public interface CourseCategoryRepository extends JpaRepository<CourseCategory, Integer>{
    List<CourseCategory> findByCourseId(Integer courseId);
    List<CourseCategory> findByCategoryId(Integer categoryId);   
}
