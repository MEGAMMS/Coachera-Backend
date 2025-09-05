package com.coachera.backend.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.coachera.backend.entity.Course;
import com.coachera.backend.entity.Material;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Integer> {
    List<Material> findBySectionIdOrderByOrderIndexAsc(Integer sectionId);

    Optional<Material> findBySectionIdAndOrderIndex(Integer sectionId, Integer orderIndex);

    boolean existsBySectionIdAndOrderIndexAndIdNot(Integer sectionId, Integer orderIndex, Integer excludeMaterialId);

    boolean existsBySectionIdAndOrderIndex(Integer sectionId, Integer orderIndex);

    @Query("SELECT COUNT(m) FROM Material m WHERE m.section.module.course = :course")
    long countByCourse(@Param("course") Course course);

    @Query("SELECT m FROM Material m WHERE m.section.module.course.id = :courseId")
    Set<Material> findByCourseId(@Param("courseId") Integer courseId);
}
