package com.coachera.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coachera.backend.entity.Course;

public interface CourseRepository extends JpaRepository<Course, Integer> {
    
    List<Course> findByOrgId(Integer orgId);
    boolean existsByTitleAndOrgId(String title, Integer orgId);
}
