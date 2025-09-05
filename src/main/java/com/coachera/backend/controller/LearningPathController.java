package com.coachera.backend.controller;

import com.coachera.backend.dto.ApiResponse;
import com.coachera.backend.dto.LearningPathDTO;
import com.coachera.backend.dto.LearningPathWithCoursersDTO;
import com.coachera.backend.dto.pagination.PaginationRequest;
import com.coachera.backend.entity.Organization;
import com.coachera.backend.entity.User;
import com.coachera.backend.service.LearningPathService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/learning-paths")
public class LearningPathController {

    private final LearningPathService learningPathService;

    @PostMapping
    @PreAuthorize("hasRole('ORGANIZATION')")
    public ApiResponse<?> createLearningPath(
            @RequestBody @Valid LearningPathDTO learningPathDTO,
            @AuthenticationPrincipal User user) {
        Organization organization = user.getOrganization();
        LearningPathDTO createdLearningPath = learningPathService.createLearningPath(learningPathDTO, organization);
        return ApiResponse.created("Learning path created successfully", createdLearningPath);
    }

    @GetMapping("/{id}")
    public ApiResponse<?> getLearningPathById(@PathVariable Integer id) {
        LearningPathDTO learningPath = learningPathService.getLearningPathById(id);
        return ApiResponse.success(learningPath);
    }

    @GetMapping("/courses/{id}")
    public ApiResponse<?> getCoursesByLearningPathId(@PathVariable Integer id) {
        return ApiResponse.success(learningPathService.getCoursesBylearningPathId(id));
    }

    @GetMapping
    public ApiResponse<?> getAllLearningPaths(@Valid PaginationRequest paginationRequest) {
        return ApiResponse.paginated(learningPathService.getLearningPaths(paginationRequest.toPageable()));
    }

    @GetMapping("/organization")
    @PreAuthorize("hasRole('ORGANIZATION')")
    public ApiResponse<?> getOrganizationLearningPaths(@AuthenticationPrincipal User user) {
        Organization organization = user.getOrganization();
        List<LearningPathDTO> learningPaths = learningPathService.getLearningPathsByOrganization(organization.getId());
        return ApiResponse.success(learningPaths);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZATION')")
    public ApiResponse<?> updateLearningPath(
            @PathVariable Integer id,
            @RequestBody @Valid LearningPathDTO learningPathDTO,
            @AuthenticationPrincipal User user) {
        Organization organization = user.getOrganization();
        LearningPathDTO updatedLearningPath = learningPathService.updateLearningPath(id, learningPathDTO, organization);
        return ApiResponse.success("Learning path updated successfully", updatedLearningPath);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZATION')")
    public ApiResponse<?> deleteLearningPath(
            @PathVariable Integer id,
            @AuthenticationPrincipal User user) {
        Organization organization = user.getOrganization();
        learningPathService.deleteLearningPath(id, organization);
        return ApiResponse.noContentResponse();
    }

    @PostMapping("/{learningPathId}/courses/{courseId}")
    @PreAuthorize("hasRole('ORGANIZATION')")
    public ApiResponse<?> addCourseToLearningPath(
            @PathVariable Integer learningPathId,
            @PathVariable Integer courseId,
            @RequestParam Integer orderIndex,
            @AuthenticationPrincipal User user) {
        Organization organization = user.getOrganization();
        LearningPathDTO updatedLearningPath = learningPathService.addCourseToLearningPath(
                learningPathId, courseId, orderIndex, organization);
        return ApiResponse.success("Course added to learning path successfully", updatedLearningPath);
    }

    @DeleteMapping("/{learningPathId}/courses/{courseId}")
    @PreAuthorize("hasRole('ORGANIZATION')")
    public ApiResponse<?> removeCourseFromLearningPath(
            @PathVariable Integer learningPathId,
            @PathVariable Integer courseId,
            @AuthenticationPrincipal User user) {
        Organization organization = user.getOrganization();
        LearningPathDTO updatedLearningPath = learningPathService.removeCourseFromLearningPath(
                learningPathId, courseId, organization);
        return ApiResponse.success("Course removed from learning path successfully", updatedLearningPath);
    }

    @GetMapping("/{id}/courses-dto")
public ApiResponse<?> getLearningPathWithCourses(@PathVariable Integer id) {
    LearningPathWithCoursersDTO dto = learningPathService.getLearningPathWithCoursesById(id);
    return ApiResponse.success(dto);
}
}