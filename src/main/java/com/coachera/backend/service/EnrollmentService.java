package com.coachera.backend.service;

import com.coachera.backend.dto.EnrollmentDTO;
import com.coachera.backend.entity.Course;
import com.coachera.backend.entity.Enrollment;
import com.coachera.backend.entity.Student;
import com.coachera.backend.exception.ResourceNotFoundException;
import com.coachera.backend.repository.CourseRepository;
import com.coachera.backend.repository.EnrollmentRepository;
import com.coachera.backend.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public List<EnrollmentDTO> getEnrollmentsByStudentId(Integer studentId) {
        return enrollmentRepository.findByStudentId(studentId)
                .stream()
                .map(EnrollmentDTO::new)
                .collect(Collectors.toList());
    }

    public List<EnrollmentDTO> getEnrollmentsByCourseId(Integer courseId) {
        return enrollmentRepository.findByCourseId(courseId)
                .stream()
                .map(EnrollmentDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public EnrollmentDTO enrollStudent(Integer studentId, Integer courseId, String progress) {
        // Check if already enrolled
        if (enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new IllegalStateException("Student is already enrolled in this course");
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setProgress(progress);
        
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        return new EnrollmentDTO(savedEnrollment);
    }

    @Transactional
    public EnrollmentDTO updateProgress(Integer studentId, Integer courseId, String progress) {
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found for studentId: " + studentId + " and courseId: " + courseId));
        
        enrollment.setProgress(progress);
        Enrollment updatedEnrollment = enrollmentRepository.save(enrollment);
        return new EnrollmentDTO(updatedEnrollment);
    }

    @Transactional
    public void unenrollStudent(Integer studentId, Integer courseId) {
        if (!enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new ResourceNotFoundException("Enrollment not found for studentId: " + studentId + " and courseId: " + courseId);
        }
        enrollmentRepository.deleteByStudentIdAndCourseId(studentId, courseId);
    }
}