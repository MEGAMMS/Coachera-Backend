package com.coachera.backend.repository;

import com.coachera.backend.entity.CourseCompletion;
import com.coachera.backend.entity.Enrollment;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseCompletionRepository extends JpaRepository<CourseCompletion, Integer> {

    // You can keep your custom methods
    Optional<CourseCompletion> findByEnrollment(Enrollment enrollment);
}