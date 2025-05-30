package com.coachera.backend.repository;

import com.coachera.backend.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Integer> {

    // Find all modules for a specific course ordered by orderIndex
    List<Module> findByCourseIdOrderByOrderIndexAsc(Integer courseId);

    // Find a module by course ID and order index
    Optional<Module> findByCourseIdAndOrderIndex(Integer courseId, Integer orderIndex);

    // Check if a module exists for a specific course and order index
    boolean existsByCourseIdAndOrderIndex(Integer courseId, Integer orderIndex);
    

    // Find the maximum order index for a course
    @Query("SELECT MAX(w.orderIndex) FROM Module w WHERE w.course.id = :courseId")
    Integer findMaxOrderIndexByCourseId(@Param("courseId") Integer courseId);
}