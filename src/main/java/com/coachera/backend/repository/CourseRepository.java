package com.coachera.backend.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.coachera.backend.entity.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {
    
    List<Course> findByOrgId(Integer orgId);
    boolean existsByTitleAndOrgId(String title, Integer orgId);
    Page<Course> findByOrgId(Integer orgId, Pageable pageable);

    @Query("SELECT c FROM Course c JOIN c.instructors ci WHERE ci.instructor.id = :instructorId")
    Page<Course> findByInstructorId(@Param("instructorId") Integer instructorId, Pageable pageable);

}
