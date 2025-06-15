package com.coachera.backend.controller;

import com.coachera.backend.dto.ApiResponse;
import com.coachera.backend.dto.EnrollmentDTO;
import com.coachera.backend.dto.MaterialCompletionDTO;
import com.coachera.backend.entity.Enrollment;
import com.coachera.backend.entity.Material;
import com.coachera.backend.service.CompletionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/completions")
@RequiredArgsConstructor
public class CompletionController {

    private final CompletionService completionService;

    @GetMapping("/enrollment/{enrollmentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR', 'ADMIN')")
    public ApiResponse<?> getCourseCompletion(@PathVariable Integer enrollmentId) {
        return ApiResponse.success(completionService.getCourseCompletion(enrollmentId));
    }

    @GetMapping("/enrollment/{enrollmentId}/materials")
    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR', 'ADMIN')")
    public ApiResponse<?> getMaterialCompletions(@PathVariable Integer enrollmentId) {
        return ApiResponse.success(completionService.getMaterialCompletions(enrollmentId));
    }

    @PostMapping("/materials/check")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<?> checkMaterialCompletion(
            @RequestParam Integer enrollmentId,
            @RequestParam Integer materialId) {
        completionService.checkMaterialCompletion(enrollmentId, materialId);
        return ApiResponse.noContentResponse();
    }

    @PostMapping("/materials/manual")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ApiResponse<?> markMaterialComplete(
            @Valid @RequestBody MaterialCompletionDTO request) {
        return ApiResponse.created(
                "Material marked as complete",
                completionService.markMaterialComplete(
                        request.getEnrollmentId(),
                        request.getMaterialId()));
    }

    // @PutMapping("/enrollment/{enrollmentId}/progress")
    // @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    // public ApiResponse<?> updateCourseProgress(
    //         @PathVariable Integer enrollmentId,
    //         @RequestParam BigDecimal progress) {
    //     return ApiResponse.success(
    //             completionService.updateCourseProgress(enrollmentId, progress));
    // }


    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ApiResponse<?> getCompletionsByCourse(@PathVariable Integer courseId) {
        return ApiResponse.success(
                completionService.getCompletionsByCourse(courseId));
    }

    // @GetMapping("/student/{studentId}")
    // @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR', 'ADMIN')")
    // public ApiResponse<?> getCompletionsByStudent(@PathVariable Integer studentId) {
    //     return ApiResponse.success(
    //             completionService.getCompletionsByStudent(studentId));
    // }

    // @PatchMapping("/enrollment/{enrollmentId}/reset")
    // @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    // public ApiResponse<?> resetCompletion(
    //         @PathVariable Integer enrollmentId) {
    //     completionService.resetCompletion(enrollmentId);
    //     return ApiResponse.noContentResponse();
    // }
}