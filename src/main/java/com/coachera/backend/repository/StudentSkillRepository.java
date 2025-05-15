package com.coachera.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coachera.backend.entity.StudentSkill;

@Repository
public interface StudentSkillRepository extends JpaRepository<StudentSkill, Integer> {
    List<StudentSkill> findByStudentId(Integer studentId);
    List<StudentSkill> findByStudentIdAndSkillId(Integer studentId, Integer skillId);
    boolean existsByStudentIdAndSkillIdAndCourseId(Integer studentId, Integer skillId, Integer courseId);
}
