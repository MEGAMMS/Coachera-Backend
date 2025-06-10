package com.coachera.backend.repository;

import com.coachera.backend.entity.Enrollment;
import com.coachera.backend.entity.Material;
import com.coachera.backend.entity.MaterialCompletion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MaterialCompletionRepository extends JpaRepository<MaterialCompletion, Long> {
    Optional<MaterialCompletion> findByEnrollmentAndMaterial(Enrollment enrollment, Material material);
    List<MaterialCompletion> findByEnrollment(Enrollment enrollment);
    long countByEnrollmentAndCompleted(Enrollment enrollment, boolean completed);
}
