package com.coachera.backend.controller;

import java.util.List;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coachera.backend.dto.StudentDTO;
import com.coachera.backend.service.StudentService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;


@RestController
@RequestMapping("/api/students")
public class StudentController {
    
 
    private final StudentService studentService;

    public StudentController(StudentService studentService)
    {
        this.studentService=studentService;
    }

    @GetMapping
    public ResponseEntity<List<StudentDTO>> getAllStudent()
    {
        return new ResponseEntity<>(studentService.getAllStudents() ,HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getStudentById(@PathVariable Integer id)
    {
        return new ResponseEntity<>(studentService.getStudentById(id),HttpStatus.OK);
    }

    @DeleteMapping("/{studentId}")
    public ResponseEntity<Void> deletStudent(@PathVariable Integer studentId)
    {
        studentService.deleteStudent(studentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping
    public ResponseEntity<StudentDTO> createStudent(@RequestBody StudentDTO studentDTO)
    {
        return new ResponseEntity<>(studentService.createStudent(studentDTO),HttpStatus.CREATED);
    }

    @PutMapping("/{studentId}")
    public ResponseEntity<StudentDTO> updateStudent(@PathVariable Integer studentId , @RequestBody StudentDTO studentDTO)
    {
        return new ResponseEntity<>(studentService.updateStudent(studentId,studentDTO ),HttpStatus.OK);
    }


}
