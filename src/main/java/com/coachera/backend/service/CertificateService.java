package com.coachera.backend.service;

import com.coachera.backend.dto.CertificateDTO;
import com.coachera.backend.entity.Certificate;
import com.coachera.backend.entity.Course;
// import com.coachera.backend.entity.Student;
import com.coachera.backend.exception.ResourceNotFoundException;
import com.coachera.backend.repository.CertificateRepository;
import com.coachera.backend.repository.CourseRepository;
// import com.coachera.backend.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final CourseRepository courseRepository;
    // private final StudentRepository studentRepository;
    private final ModelMapper modelMapper;

    public CertificateDTO createCertificate(CertificateDTO certificateDTO) {
        
        Course course = courseRepository.findById(certificateDTO.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
    

        // Student student = null;
        // if ( certificateDTO.getStudentId() != null) {
        //     student = studentRepository.findById(certificateDTO.getStudentId())
        //             .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        // }
    
        Certificate certificate = modelMapper.map(certificateDTO, Certificate.class);
        certificate.setCourse(course);
        // certificate.setStudent(student); 
        certificate.setIssuedAt(LocalDate.now());
    
        Certificate savedCertificate = certificateRepository.save(certificate);
        return modelMapper.map(savedCertificate, CertificateDTO.class);
    }

    public CertificateDTO getCertificateById(Integer id) {
        Certificate certificate = certificateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found"));
        return modelMapper.map(certificate, CertificateDTO.class);
    }

    // public List<CertificateDTO> getCertificatesByStudent(Integer studentId) {
    //     return certificateRepository.findByStudentId(studentId).stream()
    //             .map(cert -> modelMapper.map(cert, CertificateDTO.class))
    //             .toList();
    // }

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
    
       
        if (certificateDTO.getCourseId() != null) {
            Course course = courseRepository.findById(certificateDTO.getCourseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
            existingCertificate.setCourse(course);
        }
    
        
        // if (certificateDTO.getStudentId() != null) {
        //     Student student = studentRepository.findById(certificateDTO.getStudentId())
        //             .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        //     existingCertificate.setStudent(student);
        // } else {
        //     existingCertificate.setStudent(null); 
        // }
    
  
        if (certificateDTO.getCertificateUrl() != null) {
            existingCertificate.setCertificateUrl(certificateDTO.getCertificateUrl());
        }
    
       
        Certificate updatedCertificate = certificateRepository.save(existingCertificate);
        return modelMapper.map(updatedCertificate, CertificateDTO.class);
    }
}