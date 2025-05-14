package com.coachera.backend.repository;

import com.coachera.backend.entity.Week;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WeekRepository extends JpaRepository<Week, Integer> {

    // Find all weeks for a specific course ordered by orderIndex
    List<Week> findByCourseIdOrderByOrderIndexAsc(Integer courseId);

    // Find a week by course ID and order index
    Optional<Week> findByCourseIdAndOrderIndex(Integer courseId, Integer orderIndex);

    // Check if a week exists for a specific course and order index
    boolean existsByCourseIdAndOrderIndex(Integer courseId, Integer orderIndex);

    // Find the maximum order index for a course
    @Query("SELECT MAX(w.orderIndex) FROM Week w WHERE w.course.id = :courseId")
    Integer findMaxOrderIndexByCourseId(@Param("courseId") Integer courseId);
}