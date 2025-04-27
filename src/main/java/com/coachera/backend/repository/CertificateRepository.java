package com.coachera.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.coachera.backend.entity.Certificate;

public interface CertificateRepository extends JpaRepository<Certificate, Integer> {
    List<Certificate> findByStudentId(Integer studentId);
    Optional<Certificate> findByCourseIdAndStudentId(Integer courseId, Integer studentId);
}
