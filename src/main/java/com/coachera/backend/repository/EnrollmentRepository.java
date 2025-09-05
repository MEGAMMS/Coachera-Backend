package com.coachera.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coachera.backend.entity.Course;
import com.coachera.backend.entity.Enrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {
    List<Enrollment> findByStudentId(Integer studentId);

    List<Enrollment> findByCourseId(Integer courseId);

    Optional<Enrollment> findByStudentIdAndCourseId(Integer studentId, Integer courseId);

    boolean existsByStudentIdAndCourseId(Integer studentId, Integer courseId);

    boolean existsByCourse(Course course);

    void deleteByStudentIdAndCourseId(Integer studentId, Integer courseId);

    Page<Enrollment> findByStudentId(Integer studentId, Pageable pageable);
}
