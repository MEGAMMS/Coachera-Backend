package com.coachera.backend.controller;

import com.coachera.backend.dto.ApiResponse;
import com.coachera.backend.dto.EnrollmentDTO;
import com.coachera.backend.entity.User;
import com.coachera.backend.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@PreAuthorize("hasRole('STUDENT')")
@RequiredArgsConstructor
@Tag(name = "Enrollments", description = "Endpoints for managing student course enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @GetMapping("/student")
    @Operation(summary = "Get all enrollments for a student")
    public ApiResponse<?> getEnrollmentsByStudent(@AuthenticationPrincipal User user) {

        List<EnrollmentDTO> enrollments = enrollmentService.getEnrollmentsByStudent(user);
        return ApiResponse.success(enrollments);

    }

    @PostMapping("student/{courseId}")
    @Operation(summary = "Enroll a student in a course")
    public ApiResponse<?> enrollStudent(
            @AuthenticationPrincipal User user,
            @PathVariable Integer courseId,
            @RequestParam(defaultValue = "0%") String progress) {

        EnrollmentDTO enrollmentDTO = enrollmentService.enrollStudent(user, courseId, progress);
        return ApiResponse.created("Student was enrolled", enrollmentDTO);

    }


    @DeleteMapping("/delete/{courseId}/student")
    @Operation(summary = "Unenroll a student from a course")
    public ApiResponse<?> unenrollStudent(
            @AuthenticationPrincipal User user,
            @PathVariable Integer courseId) {

        enrollmentService.unenrollStudent(user, courseId);
        return ApiResponse.noContentResponse();

    }
}
