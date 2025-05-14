package com.coachera.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coachera.backend.entity.LearningPathCourse;

@Repository
public interface LearningPathCourseRepository extends JpaRepository<LearningPathCourse, Integer> {
    List<LearningPathCourse> findByLearningPathIdOrderByOrderIndexAsc(Integer learningPathId);
    Optional<LearningPathCourse> findByLearningPathIdAndCourseId(Integer learningPathId, Integer courseId);
}