package com.coachera.backend.controller;

import com.coachera.backend.dto.ApiResponse;
import com.coachera.backend.dto.StudentDTO;
import com.coachera.backend.dto.StudentRequestDTO;
import com.coachera.backend.dto.pagination.PaginationRequest;
import com.coachera.backend.entity.User;
import com.coachera.backend.service.StudentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;
    
    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping
    public ApiResponse<?> createStudent(@RequestBody @Valid StudentRequestDTO studentDTO ,@AuthenticationPrincipal User user) {

        StudentDTO createdStudent = studentService.createStudent(studentDTO ,user);
        return ApiResponse.created("Student was created successfully", createdStudent);

    }

    @GetMapping("/me")
    public ApiResponse<?> getStudent(@AuthenticationPrincipal User user) {

        StudentDTO student = studentService.getStudentByUser(user);
        return ApiResponse.success(student);

    }

    @GetMapping("/me/courses")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<?> getEnrolledCourses(@AuthenticationPrincipal User user, @Valid PaginationRequest paginationRequest) {
        return ApiResponse.paginated(studentService.getEnrolledCoursesByUser(user, paginationRequest.toPageable()));
    }

    @GetMapping("/{id}")
    public ApiResponse<?> getStudentById(@PathVariable Integer id) {

        StudentDTO student = studentService.getStudentById(id);
        return ApiResponse.success(student);

    }

    @GetMapping
    // @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<?> getAllStudents(@Valid PaginationRequest paginationRequest) {

        return ApiResponse.paginated(studentService.getStudents(paginationRequest.toPageable()));

    }

    @PutMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<?> updateStudent(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody StudentRequestDTO studentDTO) {

        StudentDTO updatedStudent = studentService.updateStudent(user, studentDTO);
        return ApiResponse.success("Student was updated successfully", updatedStudent);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<?> deleteStudent(@PathVariable Integer id) {

        studentService.deleteStudent(id);
        return ApiResponse.noContentResponse();

    }
}
