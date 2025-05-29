package com.coachera.backend.controller;

import com.coachera.backend.dto.ApiResponse;
import com.coachera.backend.dto.LearningPathDTO;
import com.coachera.backend.service.LearningPathService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/learning-paths")
public class LearningPathController {

    private final LearningPathService learningPathService;

    public LearningPathController(LearningPathService learningPathService) {
        this.learningPathService = learningPathService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('ORGANIZATION')")
    public ApiResponse<?> createLearningPath(@RequestBody @Valid LearningPathDTO learningPathDTO) {
        LearningPathDTO createdLearningPath = learningPathService.createLearningPath(learningPathDTO);
        return ApiResponse.created("Learning path created successfully", createdLearningPath);
    }

    @GetMapping("/{id}")
    public ApiResponse<?> getLearningPathById(@PathVariable Integer id) {
        LearningPathDTO learningPath = learningPathService.getLearningPathById(id);
        return ApiResponse.success(learningPath);
    }

    @GetMapping
    public ApiResponse<?> getAllLearningPaths() {
        List<LearningPathDTO> learningPaths = learningPathService.getAllLearningPaths();
        return ApiResponse.success(learningPaths);
    }

    @GetMapping("/organization/{orgId}")
    public ApiResponse<?> getLearningPathsByOrganization(@PathVariable Integer orgId) {
        List<LearningPathDTO> learningPaths = learningPathService.getLearningPathsByOrganization(orgId);
        return ApiResponse.success(learningPaths);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ORGANIZATION')")
    public ApiResponse<?> updateLearningPath(
            @PathVariable Integer id,
            @RequestBody @Valid LearningPathDTO learningPathDTO) {
        LearningPathDTO updatedLearningPath = learningPathService.updateLearningPath(id, learningPathDTO);
        return ApiResponse.success("Learning path updated successfully", updatedLearningPath);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ORGANIZATION')")
    public ApiResponse<?> deleteLearningPath(@PathVariable Integer id) {
        learningPathService.deleteLearningPath(id);
        return ApiResponse.noContent();
    }

    @PostMapping("/{learningPathId}/courses/{courseId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ORGANIZATION')")
    public ApiResponse<?> addCourseToLearningPath(
            @PathVariable Integer learningPathId,
            @PathVariable Integer courseId,
            @RequestParam Integer orderIndex) {
        LearningPathDTO updatedLearningPath = learningPathService.addCourseToLearningPath(
                learningPathId, courseId, orderIndex);
        return ApiResponse.success("Course added to learning path successfully", updatedLearningPath);
    }

    @DeleteMapping("/{learningPathId}/courses/{courseId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ORGANIZATION')")
    public ApiResponse<?> removeCourseFromLearningPath(
            @PathVariable Integer learningPathId,
            @PathVariable Integer courseId) {
        LearningPathDTO updatedLearningPath = learningPathService.removeCourseFromLearningPath(
                learningPathId, courseId);
        return ApiResponse.success("Course removed from learning path successfully", updatedLearningPath);
    }
}