package com.coachera.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coachera.backend.dto.StudentDTO;
import com.coachera.backend.entity.Student;
import com.coachera.backend.entity.User;
import com.coachera.backend.exception.ResourceNotFoundException;
import com.coachera.backend.repository.StudentRepository;
import com.coachera.backend.repository.UserRepository;

@Service
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;

    public StudentService(StudentRepository studentRepository, UserRepository userRepository) {
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
    }

    public List<StudentDTO> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public StudentDTO getStudentById(Integer id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        return convertToDto(student);
    }

    public StudentDTO createStudent(StudentDTO studentDTO) {
        Student student = new Student();
        student.setFirstName(studentDTO.getFirstName());
        student.setLastName(studentDTO.getLastName());
        student.setBirthDate(studentDTO.getBirthDate());
        student.setEducation(studentDTO.getEducation());
        student.setGender(studentDTO.getGender());

        User user = userRepository.findById(studentDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + studentDTO.getUserId()));
        student.setUser(user);

        Student savedStudent = studentRepository.save(student);
        return convertToDto(savedStudent);
    }

    public StudentDTO updateStudent(Integer id, StudentDTO studentDTO) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        
        student.setFirstName(studentDTO.getFirstName());
        student.setLastName(studentDTO.getLastName());
        student.setBirthDate(studentDTO.getBirthDate());
        student.setEducation(studentDTO.getEducation());
        student.setGender(studentDTO.getGender());

        // Only update user if a new userId is provided
        if (studentDTO.getUserId() != null && !studentDTO.getUserId().equals(student.getUser().getId())) {
            User user = userRepository.findById(studentDTO.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + studentDTO.getUserId()));
            student.setUser(user);
        }

        Student updatedStudent = studentRepository.save(student);
        return convertToDto(updatedStudent);
    }

    public void deleteStudent(Integer id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        studentRepository.delete(student);
    }

    private StudentDTO convertToDto(Student student) {
        StudentDTO dto = new StudentDTO();
        dto.setId(student.getId());
        dto.setFirstName(student.getFirstName());
        dto.setLastName(student.getLastName());
        dto.setBirthDate(student.getBirthDate());
        dto.setEducation(student.getEducation());
        dto.setGender(student.getGender());
        
        if (student.getUser() != null) {
            dto.setUserId(student.getUser().getId());
        }
        
        return dto;
    }
}