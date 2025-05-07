package com.coachera.backend.service;

import java.util.List;

import com.coachera.backend.dto.StudentDTO;




public interface StudentService {
    public List<StudentDTO>getAllStudents();
    public StudentDTO getStudentById(Integer id);
    public void deleteStudent(Integer id);
    public StudentDTO createStudent(StudentDTO studentDTO);
    public StudentDTO updateStudent(StudentDTO studentDTO,Integer id);
}
