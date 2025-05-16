package com.coachera.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.coachera.backend.entity.StudentCertificate;

public interface StudentCertificateRepository extends JpaRepository<StudentCertificate, Integer> {
    List<StudentCertificate> findByStudentId(Integer studentId);
    List<StudentCertificate> findByCertificateId(Integer certificateId);   
}
