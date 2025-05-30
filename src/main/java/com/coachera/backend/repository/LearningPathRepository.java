package com.coachera.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.coachera.backend.entity.LearningPath;

@Repository
public interface LearningPathRepository extends JpaRepository<LearningPath, Integer> {
    List<LearningPath> findByOrganizationId(Integer orgId);
    List<LearningPath> findByTitleContainingIgnoreCase(String keyword);
    // Only returns if the org matches
    @Query("SELECT lp FROM LearningPath lp WHERE lp.id = :id AND lp.organization.id = :orgId")
    Optional<LearningPath> findByIdAndOrganizationId(
        @Param("id") Integer id, 
        @Param("orgId") Integer orgId
    );
}