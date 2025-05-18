package com.coachera.backend.controller;

import com.coachera.backend.dto.ApiResponse;
import com.coachera.backend.dto.StudentDTO;
import com.coachera.backend.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping
    public ApiResponse<?> createStudent(@RequestBody @Valid StudentDTO studentDTO) {

        StudentDTO createdStudent = studentService.createStudent(studentDTO);
        return ApiResponse.created("Student was created successfully", createdStudent);

    }

    @GetMapping("/{id}")
    public ApiResponse<?> getStudent(@PathVariable Integer id) {

        StudentDTO student = studentService.getStudentById(id);
        return ApiResponse.success(student);

    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<?> getAllStudents() {

        List<StudentDTO> students = studentService.getAllStudents();
        return ApiResponse.success(students);

    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<?> updateStudent(
            @PathVariable Integer id,
            @Valid @RequestBody StudentDTO studentDTO) {

        StudentDTO updatedStudent = studentService.updateStudent(id, studentDTO);
        return ApiResponse.success("Student was updated successfully", updatedStudent);

    }

    @DeleteMapping("/{id}")
    public ApiResponse<?> deleteStudent(@PathVariable Integer id) {

        studentService.deleteStudent(id);
        return ApiResponse.noContent();

    }
}