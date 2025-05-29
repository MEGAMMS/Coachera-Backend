package com.coachera.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coachera.backend.entity.Skill;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Integer> {
    Optional<Skill> findByName(String name);
    Boolean existsByName(String name);
    List<Skill> findByNameContainingIgnoreCase(String keyword);
}