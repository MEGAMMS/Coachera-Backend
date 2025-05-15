package com.coachera.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coachera.backend.entity.LearningPath;

@Repository
public interface LearningPathRepository extends JpaRepository<LearningPath, Integer> {
    List<LearningPath> findByOrganizationId(Integer orgId);
    List<LearningPath> findByTitleContainingIgnoreCase(String keyword);
}