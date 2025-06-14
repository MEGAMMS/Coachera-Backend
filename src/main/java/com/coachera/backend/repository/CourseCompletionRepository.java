package com.coachera.backend.repository;

import com.coachera.backend.entity.CourseCompletion;
import com.coachera.backend.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseCompletionRepository extends JpaRepository<CourseCompletion, Enrollment> {
    
}
