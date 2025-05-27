package com.coachera.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coachera.backend.entity.Section;

@Repository
public interface SectionRepository extends JpaRepository<Section, Integer> {
    List<Section> findByModuleIdOrderByOrderIndexAsc(Integer moduleId);
    Optional<Section> findByModuleIdAndOrderIndex(Integer moduleId, Integer orderIndex);
    boolean existsByModuleIdAndOrderIndexAndIdNot(Integer moduleId, Integer orderIndex, Integer excludeSectionId);
    boolean existsByModuleIdAndOrderIndex(Integer moduleId, Integer orderIndex);
}
