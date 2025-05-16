package com.coachera.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.coachera.backend.entity.Certificate;

public interface CertificateRepository extends JpaRepository<Certificate, Integer> {
    // List<Certificate> findByStudentId(Integer studentId);
    // Optional<Certificate> findByCourseIdAndStudentId(Integer courseId, Integer studentId);
    // boolean existsByCourseIdAndStudentId(Integer courseId, Integer studentId);
    List<Certificate> findByCourseId(Integer courseId);

    // @Query("SELECT DISTINCT c FROM Certificate c LEFT JOIN FETCH c.students")
    // List<Certificate> findAllWithStudents();

    // @Query("SELECT c FROM Certificate c JOIN c.students s WHERE s.id = :studentId")
    // List<Certificate> findByStudentId(@Param("studentId") Integer studentId);

    // @Query("SELECT c FROM Certificate c LEFT JOIN FETCH c.students WHERE c.id = :id")
    // Optional<Certificate> findByIdWithStudents(@Param("id") Integer id);

}
