package com.coachera.backend.controller;

import com.coachera.backend.dto.ApiResponse;
import com.coachera.backend.dto.EnrollmentDTO;
import com.coachera.backend.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@PreAuthorize("hasRole('STUDENT')")
@RequiredArgsConstructor
@Tag(name = "Enrollments", description = "Endpoints for managing student course enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get all enrollments for a student")
    public ApiResponse<?> getEnrollmentsByStudentId(@PathVariable Integer studentId) {

        List<EnrollmentDTO> enrollments = enrollmentService.getEnrollmentsByStudentId(studentId);
        return ApiResponse.success(enrollments);

    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "Get all enrollments for a course")
    public ApiResponse<?> getEnrollmentsByCourseId(@PathVariable Integer courseId) {

        List<EnrollmentDTO> enrollments = enrollmentService.getEnrollmentsByCourseId(courseId);
        return ApiResponse.success(enrollments);

    }

    @PostMapping("/{courseId}/{studentId}")
    @Operation(summary = "Enroll a student in a course")
    public ApiResponse<?> enrollStudent(
            @PathVariable Integer studentId,
            @PathVariable Integer courseId,
            @RequestParam(defaultValue = "0%") String progress) {

        EnrollmentDTO enrollmentDTO = enrollmentService.enrollStudent(studentId, courseId, progress);
        return ApiResponse.created("Student was enrolled", enrollmentDTO);

    }

    @DeleteMapping("/delete/{courseId}/{studentId}")
    @Operation(summary = "Unenroll a student from a course")
    public ApiResponse<?> unenrollStudent(
            @PathVariable Integer studentId,
            @PathVariable Integer courseId) {

        enrollmentService.unenrollStudent(studentId, courseId);
        return ApiResponse.noContent();

    }
}