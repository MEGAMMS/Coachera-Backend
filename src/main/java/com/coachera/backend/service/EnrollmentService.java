package com.coachera.backend.service;

import com.coachera.backend.dto.EnrolledCourseDTO;
import com.coachera.backend.dto.EnrollmentDTO;
import com.coachera.backend.entity.Course;
import com.coachera.backend.entity.CourseCompletion;
import com.coachera.backend.entity.Enrollment;
import com.coachera.backend.entity.Material;
import com.coachera.backend.entity.Student;
import com.coachera.backend.entity.User;
import com.coachera.backend.exception.ResourceNotFoundException;
import com.coachera.backend.repository.CourseRepository;
import com.coachera.backend.repository.EnrollmentRepository;
import com.coachera.backend.repository.MaterialRepository;
import com.coachera.backend.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final MaterialRepository materialRepository;

    public List<EnrolledCourseDTO> getEnrollmentsByStudent(User user) {
        Integer studentId = studentRepository.findByUserId(user.getId()).getId();
        return enrollmentRepository.findByStudentId(studentId)
            .stream()
            .map(enrollment -> {
                Set<Material> allMaterials = materialRepository.findByCourseId(enrollment.getCourse().getId());
                return new EnrolledCourseDTO(enrollment, allMaterials);
            })
            .collect(Collectors.toList());
    }

    public List<EnrollmentDTO> getEnrollmentsByCourseId(Integer courseId) {
        return enrollmentRepository.findByCourseId(courseId)
                .stream()
                .map(EnrollmentDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public EnrollmentDTO enrollStudent(User user, Integer courseId, BigDecimal progress) {
        Integer studentId = studentRepository.findByUserId(user.getId()).getId();

        if (enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new IllegalStateException("Student is already enrolled in this course");
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        // Create new enrollment
        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .build();

        // Create and set course completion
        CourseCompletion courseCompletion = new CourseCompletion();
        courseCompletion.setEnrollment(enrollment);
        courseCompletion.setProgress(progress != null ? progress : BigDecimal.ZERO);
        enrollment.setCourseCompletion(courseCompletion);

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        return new EnrollmentDTO(savedEnrollment);
    }

    @Transactional
    public EnrollmentDTO updateProgress(Integer studentId, Integer courseId, BigDecimal progress) {
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Enrollment not found for studentId: " + studentId + " and courseId: " + courseId));

        if (enrollment.getCourseCompletion() == null) {
            CourseCompletion courseCompletion = new CourseCompletion();
            courseCompletion.setEnrollment(enrollment);
            courseCompletion.setProgress(progress);
            enrollment.setCourseCompletion(courseCompletion);
        } else {
            enrollment.getCourseCompletion().setProgress(progress);
        }

        Enrollment updatedEnrollment = enrollmentRepository.save(enrollment);
        return new EnrollmentDTO(updatedEnrollment);
    }

    @Transactional
    public void unenrollStudent(User user, Integer courseId) {
        Integer studentId = studentRepository.findByUserId(user.getId()).getId();
        if (!enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new ResourceNotFoundException(
                    "Enrollment not found for studentId: " + studentId + " and courseId: " + courseId);
        }
        enrollmentRepository.deleteByStudentIdAndCourseId(studentId, courseId);
    }

    public Enrollment getEnrollmentById(Integer enrollmentId) {
        return enrollmentRepository.findById(enrollmentId).orElse(null);

    }
    public long countenrollments() {
        return enrollmentRepository.count();
    }
}