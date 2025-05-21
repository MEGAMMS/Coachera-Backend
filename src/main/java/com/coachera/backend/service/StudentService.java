package com.coachera.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coachera.backend.dto.StudentDTO;
import com.coachera.backend.entity.Student;
import com.coachera.backend.entity.User;
import com.coachera.backend.exception.ConflictException;
import com.coachera.backend.exception.ResourceNotFoundException;
import com.coachera.backend.repository.StudentRepository;
import com.coachera.backend.repository.UserRepository;

@Service
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public StudentService(StudentRepository studentRepository, 
                        UserRepository userRepository,
                        ModelMapper modelMapper) {
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    public StudentDTO createStudent(StudentDTO studentDTO) {
        
        User user = userRepository.findById(studentDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + studentDTO.getUserId()));
    

        if (studentRepository.existsByUserId(studentDTO.getUserId())) {
            throw new ConflictException("User already has a student profile");
        }
    
        
        Student student = new Student();
        student.setUser(user);
        student.setFirstName(studentDTO.getFirstName());
        student.setLastName(studentDTO.getLastName());
        student.setBirthDate(studentDTO.getBirthDate());
        student.setGender(studentDTO.getGender());
        student.setEducation(studentDTO.getEducation());
        student.setWallet(studentDTO.getWallet());
    
        
        Student savedStudent = studentRepository.save(student);
        return convertToDto(savedStudent);
    }

    public StudentDTO getStudentByUser(User user) {
        Integer studentId = studentRepository.findByUserId(user.getId()).getId();
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        return modelMapper.map(student, StudentDTO.class);
    }

    public List<StudentDTO> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(student -> modelMapper.map(student, StudentDTO.class))
                .collect(Collectors.toList());
    }

    public StudentDTO updateStudent(User userA , StudentDTO studentDTO) {
        Integer studentId = studentRepository.findByUserId(userA.getId()).getId();
        Student existingStudent = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        modelMapper.map(studentDTO, existingStudent);
        
        
        if (studentDTO.getUserId() != null && 
            !studentDTO.getUserId().equals(existingStudent.getUser().getId())) {
            User user = userRepository.findById(studentDTO.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + studentDTO.getUserId()));
            existingStudent.setUser(user);
        }

        Student updatedStudent = studentRepository.save(existingStudent);
        return modelMapper.map(updatedStudent, StudentDTO.class);
    }

    public void deleteStudent(Integer id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student not found with id: " + id);
        }
        studentRepository.deleteById(id);
    }
    private StudentDTO convertToDto(Student student) {
        StudentDTO dto = new StudentDTO();
        dto.setId(student.getId());
        dto.setUserId(student.getUser().getId());
        dto.setFirstName(student.getFirstName());
        dto.setLastName(student.getLastName());
        dto.setBirthDate(student.getBirthDate());
        dto.setGender(student.getGender());
        dto.setEducation(student.getEducation());
        dto.setCreatedAt(student.getCreatedAt());
        dto.setUpdatedAt(student.getUpdatedAt());
        return dto;
    }
}