package com.coachera.backend.service;

import com.coachera.backend.dto.CertificateDTO;
import com.coachera.backend.dto.StudentDTO;
import com.coachera.backend.entity.*;
import com.coachera.backend.exception.ResourceNotFoundException;
import com.coachera.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final StudentCertificateRepository studentCertificateRepository;
    private final ModelMapper modelMapper;

    public CertificateDTO createCertificate(CertificateDTO certificateDTO) {
        Course course = courseRepository.findById(certificateDTO.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        Certificate certificate = Certificate.builder()
                .course(course)
                .issuedAt(certificateDTO.getIssuedAt() != null ? 
                    certificateDTO.getIssuedAt() : LocalDate.now())
                .certificateUrl(certificateDTO.getCertificateUrl())
                .certificateNumber(certificateDTO.getCertificateNumber())
                .validityYears(certificateDTO.getValidityYears())
                .build();

        Certificate savedCertificate = certificateRepository.save(certificate);

        // Add students through the join entity
        if (certificateDTO.getStudentIds() != null && !certificateDTO.getStudentIds().isEmpty()) {
            Set<Student> students = studentRepository.findAllById(certificateDTO.getStudentIds())
                    .stream().collect(Collectors.toSet());
            
            students.forEach(student -> savedCertificate.addStudent(student));
        }

        return modelMapper.map(certificateRepository.save(savedCertificate), CertificateDTO.class);
    }

    public CertificateDTO addStudents(Integer certificateId, Set<Integer> studentIds) {
        Certificate certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found"));

        Set<Student> newStudents = studentRepository.findAllById(studentIds)
                .stream().collect(Collectors.toSet());

        newStudents.forEach(student -> {
            if (!certificate.getStudentCertificates().stream()
                    .anyMatch(sc -> sc.getStudent().getId().equals(student.getId()))) {
                certificate.addStudent(student);
            }
        });

        return modelMapper.map(certificateRepository.save(certificate), CertificateDTO.class);
    }

    public CertificateDTO getCertificateById(Integer id) {
        Certificate certificate = certificateRepository.findByIdWithStudents(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found"));
        
        CertificateDTO dto = modelMapper.map(certificate, CertificateDTO.class);
        dto.setStudentIds(certificate.getStudentCertificates().stream()
                .map(sc -> sc.getStudent().getId())
                .collect(Collectors.toSet()));
        
        return dto;
    }

    public List<CertificateDTO> getCertificatesByStudent(Integer studentId) {
        return studentCertificateRepository.findByStudentId(studentId).stream()
                .map(StudentCertificate::getCertificate)
                .map(cert -> modelMapper.map(cert, CertificateDTO.class))
                .collect(Collectors.toList());
    }

    public List<CertificateDTO> getCertificatesByCourse(Integer courseId) {
        return certificateRepository.findByCourseId(courseId).stream()
                .map(cert -> {
                    CertificateDTO dto = modelMapper.map(cert, CertificateDTO.class);
                    dto.setStudentIds(cert.getStudentCertificates().stream()
                            .map(sc -> sc.getStudent().getId())
                            .collect(Collectors.toSet()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public void deleteCertificate(Integer id) {
        Certificate certificate = certificateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found"));
        
        // Remove all student associations first
        new HashSet<>(certificate.getStudentCertificates()).forEach(sc -> 
            certificate.removeStudent(sc.getStudent()));
        
        certificateRepository.delete(certificate);
    }

    public CertificateDTO updateCertificate(Integer id, CertificateDTO certificateDTO) {
        Certificate existingCertificate = certificateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found"));

        if (certificateDTO.getCourseId() != null) {
            Course course = courseRepository.findById(certificateDTO.getCourseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
            existingCertificate.setCourse(course);
        }

        if (certificateDTO.getCertificateUrl() != null) {
            existingCertificate.setCertificateUrl(certificateDTO.getCertificateUrl());
        }

        if (certificateDTO.getCertificateNumber() != null) {
            existingCertificate.setCertificateNumber(certificateDTO.getCertificateNumber());
        }

        if (certificateDTO.getValidityYears() != null) {
            existingCertificate.setValidityYears(certificateDTO.getValidityYears());
        }

        if (certificateDTO.getIssuedAt() != null) {
            existingCertificate.setIssuedAt(certificateDTO.getIssuedAt());
        }

        // Handle student updates
        if (certificateDTO.getStudentIds() != null) {
            // Get current students
            Set<Integer> currentStudentIds = existingCertificate.getStudentCertificates().stream()
                    .map(sc -> sc.getStudent().getId())
                    .collect(Collectors.toSet());

            // Students to add
            Set<Integer> newStudentIds = new HashSet<>(certificateDTO.getStudentIds());
            newStudentIds.removeAll(currentStudentIds);
            
            // Students to remove
            Set<Integer> removedStudentIds = new HashSet<>(currentStudentIds);
            removedStudentIds.removeAll(certificateDTO.getStudentIds());

            // Add new students
            studentRepository.findAllById(newStudentIds).forEach(student -> 
                existingCertificate.addStudent(student));

            // Remove students
            existingCertificate.getStudentCertificates().removeIf(sc -> 
                removedStudentIds.contains(sc.getStudent().getId()));
        }

        return modelMapper.map(certificateRepository.save(existingCertificate), CertificateDTO.class);
    }

    public void removeStudentFromCertificate(Integer certificateId, Integer studentId) {
        Certificate certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found"));
        
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        
        certificate.removeStudent(student);
        certificateRepository.save(certificate);
    }

    public List<StudentDTO> getStudentsByCertificateId(Integer certificateId) {
        Certificate certificate = certificateRepository.findByIdWithStudents(certificateId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found"));
        
        return certificate.getStudentCertificates().stream()
                .map(StudentCertificate::getStudent)
                .map(student -> modelMapper.map(student, StudentDTO.class))
                .collect(Collectors.toList());
    }

    public CertificateDTO updateIssuedDate(Integer id, LocalDate newDate) {
        Certificate certificate = certificateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found"));
        
        certificate.setIssuedAt(newDate);
        return modelMapper.map(certificateRepository.save(certificate), CertificateDTO.class);
    }

}