package com.coachera.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.coachera.backend.entity.Enrollment;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {
    List<Enrollment> findByStudentId(Integer studentId);
    Optional<Enrollment> findByCourseIdAndStudentId(Integer courseId, Integer studentId);
}
