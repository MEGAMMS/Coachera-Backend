package com.coachera.backend.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coachera.backend.dto.ApiResponse;
import com.coachera.backend.dto.EnrollmentDTO;
import com.coachera.backend.service.EnrollmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/enrollments")
@PreAuthorize("hasRole('ORGANIZATION')")
@RequiredArgsConstructor
@Tag(name = "OrganizationEnrollments", description = "Endpoints for managing Organization course enrollments")
public class OrganizationEnrollmentController {

    private final EnrollmentService enrollmentService;

    @GetMapping("course/{courseId}")
    @Operation(summary = "Get all enrollments for a course")
    public ApiResponse<?> getEnrollmentsByCourseId(@PathVariable Integer courseId) {

        List<EnrollmentDTO> enrollments = enrollmentService.getEnrollmentsByCourseId(courseId);
        return ApiResponse.success(enrollments);

    }

}
