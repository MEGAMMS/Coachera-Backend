package com.coachera.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.coachera.backend.entity.Certificate;

public interface CertificateRepository extends JpaRepository<Certificate, Integer> {
    
    // Find certificates by course ID
    List<Certificate> findByCourseId(Integer courseId);
    
    // Find all certificates with student relationships (eager loading)
    @Query("SELECT DISTINCT c FROM Certificate c LEFT JOIN FETCH c.studentCertificates sc LEFT JOIN FETCH sc.student")
    List<Certificate> findAllWithStudents();
    
    // Find certificates by student ID (through join entity)
    @Query("SELECT c FROM Certificate c JOIN c.studentCertificates sc WHERE sc.student.id = :studentId")
    List<Certificate> findByStudentId(@Param("studentId") Integer studentId);
    
    // Find certificate by ID with student relationships (eager loading)
    @Query("SELECT c FROM Certificate c LEFT JOIN FETCH c.studentCertificates sc LEFT JOIN FETCH sc.student WHERE c.id = :id")
    Optional<Certificate> findByIdWithStudents(@Param("id") Integer id);
    
    // Check if certificate exists for specific course and student
    @Query("SELECT CASE WHEN COUNT(sc) > 0 THEN true ELSE false END " +
           "FROM StudentCertificate sc " +
           "WHERE sc.certificate.course.id = :courseId AND sc.student.id = :studentId")
    boolean existsByCourseIdAndStudentId(@Param("courseId") Integer courseId, 
                                        @Param("studentId") Integer studentId);
    
    // Find certificate by course and student (through join entity)
    @Query("SELECT sc.certificate FROM StudentCertificate sc " +
           "WHERE sc.certificate.course.id = :courseId AND sc.student.id = :studentId")
    Optional<Certificate> findByCourseIdAndStudentId(@Param("courseId") Integer courseId, 
                                                   @Param("studentId") Integer studentId);
}