package com.coachera.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coachera.backend.entity.Section;

@Repository
public interface SectionRepository extends JpaRepository<Section, Integer> {
    List<Section> findByWeekIdOrderByOrderIndexAsc(Integer weekId);
    Optional<Section> findByWeekIdAndOrderIndex(Integer weekId, Integer orderIndex);
    boolean existsByWeekIdAndOrderIndexAndIdNot(Integer weekId, Integer orderIndex, Integer excludeSectionId);
    boolean existsByWeekIdAndOrderIndex(Integer weekId, Integer orderIndex);
}
