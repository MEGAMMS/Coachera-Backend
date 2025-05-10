package com.coachera.backend.service;

import com.coachera.backend.dto.CertificateDTO;
import com.coachera.backend.dto.StudentDTO;
import com.coachera.backend.entity.Certificate;
import com.coachera.backend.entity.Course;
import com.coachera.backend.entity.Student;
// import com.coachera.backend.entity.Student;
import com.coachera.backend.exception.ResourceNotFoundException;
import com.coachera.backend.repository.CertificateRepository;
import com.coachera.backend.repository.CourseRepository;
import com.coachera.backend.repository.StudentRepository;

// import com.coachera.backend.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final ModelMapper modelMapper;

    public CertificateDTO createCertificate(CertificateDTO certificateDTO) {
    Course course = courseRepository.findById(certificateDTO.getCourseId())
            .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

    Certificate certificate = new Certificate();
    certificate.setCourse(course);
    certificate.setIssuedAt(LocalDate.now());
    certificate.setCertificateUrl(certificateDTO.getCertificateUrl());

    
    if (certificateDTO.getStudentIds() != null && !certificateDTO.getStudentIds().isEmpty()) {
        List<Student> students = studentRepository.findAllById(certificateDTO.getStudentIds());
        certificate.setStudents(students);
    } else {
        certificate.setStudents(Collections.emptyList()); 
    }

      Certificate saved = certificateRepository.save(certificate);
        Certificate withStudents = certificateRepository.findByIdWithStudents(saved.getId())
        .orElseThrow(() -> new ResourceNotFoundException("Certificate not found after creation"));
  
    
    return new CertificateDTO(withStudents);
}

     public CertificateDTO addStudents(Integer certificateId, List<Integer> studentIds) {
        Certificate certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found"));

        List<Student> newStudents = studentRepository.findAllById(studentIds);
        certificate.getStudents().addAll(newStudents);

        Certificate updated = certificateRepository.save(certificate);
        return new CertificateDTO(updated);
    }

    public CertificateDTO getCertificateById(Integer id) {
        Certificate certificate = certificateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found"));
        return modelMapper.map(certificate, CertificateDTO.class);
    }

    public List<CertificateDTO> getCertificatesByStudent(Integer studentId) {
        return certificateRepository.findByStudentId(studentId).stream()
                .map(cert -> modelMapper.map(cert, CertificateDTO.class))
                .toList();
    }

    public List<CertificateDTO> getCertificatesByCourse(Integer courseId) {
        return certificateRepository.findByCourseId(courseId).stream()
                .map(cert -> modelMapper.map(cert, CertificateDTO.class))
                .toList();
    }

    public void deleteCertificate(Integer id) {
        if (!certificateRepository.existsById(id)) {
            throw new ResourceNotFoundException("Certificate not found");
        }
        certificateRepository.deleteById(id);
    }

   public CertificateDTO updateCertificate(Integer id, CertificateDTO certificateDTO) {
    Certificate existingCertificate = certificateRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Certificate not found with id: " + id));

    if (certificateDTO.getCourseId() != null && 
        !certificateDTO.getCourseId().equals(existingCertificate.getCourse().getId())) {
        Course course = courseRepository.findById(certificateDTO.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        existingCertificate.setCourse(course);
    }


    if (certificateDTO.getStudentIds() != null) {
        List<Student> students = studentRepository.findAllById(certificateDTO.getStudentIds());
        if (students.size() != certificateDTO.getStudentIds().size()) {
            throw new ResourceNotFoundException("One or more students not found");
        }
        existingCertificate.setStudents(students);
    }

    if (certificateDTO.getCertificateUrl() != null) {
        existingCertificate.setCertificateUrl(certificateDTO.getCertificateUrl());
    }

    Certificate updatedCertificate = certificateRepository.save(existingCertificate);
    return modelMapper.map(updatedCertificate, CertificateDTO.class);
}


public void removeStudentFromCertificate(Integer certificateId, Integer studentId) {
    Certificate certificate = certificateRepository.findById(certificateId)
            .orElseThrow(() -> new ResourceNotFoundException("Certificate not found"));
    
    certificate.getStudents().removeIf(student -> student.getId().equals(studentId));
    certificateRepository.save(certificate);
}

public List<StudentDTO> getStudentsByCertificateId(Integer certificateId) {
    Certificate certificate = certificateRepository.findById(certificateId)
            .orElseThrow(() -> new ResourceNotFoundException("Certificate not found"));
    
    return certificate.getStudents().stream()
            .map(student -> modelMapper.map(student, StudentDTO.class))
            .collect(Collectors.toList());
}


public CertificateDTO updateIssuedDate(Integer id, LocalDate newDate) {
    Certificate certificate = certificateRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Certificate not found"));
    
    certificate.setIssuedAt(newDate);
    Certificate updated = certificateRepository.save(certificate);
    return modelMapper.map(updated, CertificateDTO.class);
}

    public List<CertificateDTO> findAll()
    {
        return certificateRepository.findAllWithStudents().stream()
        .map(cer -> modelMapper.map(cer, CertificateDTO.class)).
        toList();
    }
}