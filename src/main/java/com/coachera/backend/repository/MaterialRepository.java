package com.coachera.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coachera.backend.entity.Material;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Integer> {
    List<Material> findBySectionIdOrderByOrderIndexAsc(Integer sectionId);
    Optional<Material> findBySectionIdAndOrderIndex(Integer sectionId, Integer orderIndex);
    boolean existsBySectionIdAndOrderIndex(Integer sectionId, Integer orderIndex);
}
