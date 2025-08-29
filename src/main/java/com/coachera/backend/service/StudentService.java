package com.coachera.backend.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coachera.backend.dto.CourseDTO;
import com.coachera.backend.dto.StudentDTO;
import com.coachera.backend.dto.StudentRequestDTO;
import com.coachera.backend.entity.Student;
import com.coachera.backend.entity.User;
import com.coachera.backend.exception.ConflictException;
import com.coachera.backend.exception.ResourceNotFoundException;
import com.coachera.backend.repository.AccessTokenRepository;
import com.coachera.backend.repository.EnrollmentRepository;
import com.coachera.backend.repository.FavoriteRepository;
import com.coachera.backend.repository.ReviewRepository;
import com.coachera.backend.repository.StudentRepository;
import com.coachera.backend.repository.UserRepository;

@Service
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final EnrollmentRepository enrollmentRepository;
    private final FavoriteRepository favoriteRepository;
    private final ReviewRepository reviewRepository;
    private final AccessTokenRepository accessTokenRepository;

    public StudentService(StudentRepository studentRepository, 
                        UserRepository userRepository,
                        ModelMapper modelMapper,
                        EnrollmentRepository enrollmentRepository,
                        FavoriteRepository favoriteRepository,
                        ReviewRepository reviewRepository,
                        AccessTokenRepository accessTokenRepository) {
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.enrollmentRepository = enrollmentRepository;
        this.favoriteRepository = favoriteRepository;
        this.reviewRepository = reviewRepository;
        this.accessTokenRepository =accessTokenRepository;
    }

    public StudentDTO createStudent(StudentRequestDTO studentDTO, User user) {

        if (!userRepository.findById(user.getId()).isPresent()) {
            throw new IllegalArgumentException("User must be saved before creating student profile");
        }
        if (studentRepository.existsByUserId(user.getId())) {
            throw new ConflictException("User already has a student profile");
        }

        Student student = new Student();
        student.setUser(user);
        student.setFirstName(studentDTO.getFirstName());
        student.setLastName(studentDTO.getLastName());
        student.setBirthDate(studentDTO.getBirthDate());
        student.setGender(studentDTO.getGender());
        student.setEducation(studentDTO.getEducation());
        
        // Handle wallet - set to 0 
        student.setWallet(BigDecimal.ZERO);
        
        // Set other optional fields
        student.setPhoneNumber(studentDTO.getPhoneNumber());
        student.setAddress(studentDTO.getAddress());
        
        Student savedStudent = studentRepository.save(student);
        
        // Use the basic constructor that doesn't load collections
        return new StudentDTO(savedStudent);
    }

    public StudentDTO getStudentByUser(User user) {
        Integer studentId = studentRepository.findByUserId(user.getId()).getId();
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        return new StudentDTO(student);
    }

    public StudentDTO getStudentById(Integer id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        return new StudentDTO(student);
    }

    public List<StudentDTO> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(student -> new StudentDTO(student))
                .collect(Collectors.toList());
    }

    public Page<StudentDTO> getStudents(Pageable pageable) {
        return studentRepository.findAll(pageable)
                .map(StudentDTO::new);
    }

    public StudentDTO updateStudent(User user , StudentRequestDTO studentDTO) {
        if (!userRepository.findById(user.getId()).isPresent()) {
            throw new IllegalArgumentException("User must be saved before creating student profile");
        }
        Student existingStudent = studentRepository.findById(user.getStudent().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + user.getStudent().getId()));

        modelMapper.map(studentDTO, existingStudent);

        Student updatedStudent = studentRepository.save(existingStudent);
        return new StudentDTO(updatedStudent);
    }

    
    public void deleteStudent(Integer id) {
        
       Student student = studentRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

            // Delete all student-related entities
        favoriteRepository.deleteAll(favoriteRepository.findByStudentId(id));
        enrollmentRepository.deleteAll(enrollmentRepository.findByStudentId(id));
        reviewRepository.deleteAll(reviewRepository.findByStudentId(id));
        // Clear certificates and skills
        student.getStudentCertificates().clear();
        student.getStudentSkills().clear();
        studentRepository.save(student);
        if (student.getUser() != null) {
        User user = student.getUser();
           
        // Delete access tokens first
        accessTokenRepository.deleteByUserId(user.getId());

        
        
        // Break the bidirectional relationship
        user.setStudent(null);
        userRepository.save(user);
        }
        // Delete the student
        studentRepository.delete(student);
        if (student.getUser() != null) {
            // Delete the user (now that access tokens are removed)
            userRepository.delete(student.getUser());
        }

    }
    
    
    public Page<CourseDTO> getEnrolledCoursesByUser(User user, Pageable pageable) {
        Integer studentId = studentRepository.findByUserId(user.getId()).getId();
        return enrollmentRepository.findByStudentId(studentId, pageable)
                .map(enrollment -> new CourseDTO(enrollment.getCourse()));
    }

     public long countStudents() {
        return studentRepository.count();
    }
}