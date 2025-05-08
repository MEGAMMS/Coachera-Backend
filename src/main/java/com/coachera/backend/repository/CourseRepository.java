package com.coachera.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coachera.backend.entity.Course;

public interface CourseRepository extends JpaRepository<Course, Integer> {
    // List<Course> findByOrgId(Integer orgId);
}
