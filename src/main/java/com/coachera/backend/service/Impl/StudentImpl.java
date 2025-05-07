package com.coachera.backend.service.Impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coachera.backend.dto.StudentDTO;
import com.coachera.backend.entity.Student;
import com.coachera.backend.exception.ResourceNotFoundException;
import com.coachera.backend.repository.StudentRepository;
import com.coachera.backend.service.StudentService;

@Service
@Transactional
public class StudentImpl implements StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Override
    public List<StudentDTO> getAllStudents()
    {

        return studentRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
    }

    @Override
    public StudentDTO getStudentById(Integer id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        return convertToDto(student);
    }

    @Override
    public void deleteStudent(Integer id)
     {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        studentRepository.delete(student);
    }

    @Override
    public StudentDTO createStudent(StudentDTO studentDTO) {
        Student student = new Student();
        student.setFirstName(studentDTO.getFirstName());
        student.setLastName(studentDTO.getLastName());
        student.setBirthDate(studentDTO.getBirthDate());
        student.setEducation(studentDTO.getEducation());
        student.setGender(studentDTO.getGender());
        Student savedStudent = studentRepository.save(student);
        return convertToDto(savedStudent);
    }

    @Override
    public StudentDTO updateStudent(StudentDTO studentDTO, Integer id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
                student.setFirstName(studentDTO.getFirstName());
                student.setLastName(studentDTO.getLastName());
                student.setBirthDate(studentDTO.getBirthDate());
                student.setEducation(studentDTO.getEducation());
                student.setGender(studentDTO.getGender());
                Student savedStudent = studentRepository.save(student);
        return convertToDto(savedStudent);
    }

    private StudentDTO convertToDto(Student student)
    {
        StudentDTO dto = new StudentDTO();
        dto.setId(student.getId());
        dto.setFirstName(student.getFirstName());
        dto.setLastName(student.getLastName());
        dto.setBirthDate(student.getBirthDate());
        dto.setEducation(dto.getEducation());
        dto.setGender(dto.getGender());

        return dto;
    }


}
