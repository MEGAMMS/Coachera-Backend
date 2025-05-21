package com.coachera.backend.controller;

import com.coachera.backend.dto.ApiResponse;
import com.coachera.backend.dto.StudentDTO;
import com.coachera.backend.entity.User;
import com.coachera.backend.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }
    
    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping
    public ApiResponse<?> createStudent(@RequestBody @Valid StudentDTO studentDTO ,@AuthenticationPrincipal User user) {

        StudentDTO createdStudent = studentService.createStudent(studentDTO ,user);
        return ApiResponse.created("Student was created successfully", createdStudent);

    }

    @GetMapping("/user")
    public ApiResponse<?> getStudent(@AuthenticationPrincipal User user) {

        StudentDTO student = studentService.getStudentByUser(user);
        return ApiResponse.success(student);

    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<?> getAllStudents() {

        List<StudentDTO> students = studentService.getAllStudents();
        return ApiResponse.success(students);

    }

    @PutMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<?> updateStudent(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody StudentDTO studentDTO) {

        StudentDTO updatedStudent = studentService.updateStudent(user, studentDTO);
        return ApiResponse.success("Student was updated successfully", updatedStudent);

    }

    @DeleteMapping("/{id}")
    public ApiResponse<?> deleteStudent(@PathVariable Integer id) {

        studentService.deleteStudent(id);
        return ApiResponse.noContent();

    }
}