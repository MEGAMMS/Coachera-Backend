package com.coachera.backend.controller;

import com.coachera.backend.dto.ApiResponse;
import com.coachera.backend.dto.CourseCreationDTO;
import com.coachera.backend.dto.CourseDTO;
import com.coachera.backend.dto.CourseInstructorDTO;
import com.coachera.backend.dto.CourseWithModulesDTO;
import com.coachera.backend.dto.pagination.PaginatedResponse;
import com.coachera.backend.dto.pagination.PaginationRequest;
import com.coachera.backend.entity.User;
import com.coachera.backend.service.CourseService;
import com.coachera.backend.service.InstructorService;
import com.coachera.backend.service.CourseRecommendationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.nio.file.AccessDeniedException;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final InstructorService instructorService;
    private final CourseRecommendationService courseRecommendationService;
    

    @GetMapping
    public ApiResponse<PaginatedResponse<CourseDTO>> getAllCourses(@Valid PaginationRequest paginationRequest) {
        return ApiResponse.paginated(courseService.getCourses(paginationRequest.toPageable()));
    }

    @PostMapping
    @PreAuthorize("hasRole('ORGANIZATION')")
    public ApiResponse<?> createCourse(@Valid @RequestBody CourseCreationDTO courseDTO, @AuthenticationPrincipal User user) {
        CourseDTO createdCourse = courseService.createCourse(courseDTO, user);
        return ApiResponse.created("Course was created", createdCourse);
    }

    @GetMapping("/{id}")
    public ApiResponse<?> getCourse(@PathVariable Integer id) {

        CourseWithModulesDTO course = courseService.getCourseById(id);
        return ApiResponse.success(course);
    }

    @GetMapping("/recommended")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<PaginatedResponse<CourseDTO>> getRecommendedCourses(@AuthenticationPrincipal User user, @Valid PaginationRequest paginationRequest) {
        return ApiResponse.paginated(courseRecommendationService.getRecommendedCourses(user, paginationRequest.toPageable()));
    }

    @GetMapping("/popular")
    public ApiResponse<PaginatedResponse<CourseDTO>> getPopularCourses(@Valid PaginationRequest paginationRequest) {
        return ApiResponse.paginated(courseRecommendationService.getPopularCourses(paginationRequest.toPageable()));
    }

    @GetMapping("/trending")
    public ApiResponse<PaginatedResponse<CourseDTO>> getTrendingCourses(@Valid PaginationRequest paginationRequest) {
        return ApiResponse.paginated(courseRecommendationService.getTrendingCourses(paginationRequest.toPageable()));
    }

    @GetMapping("/category/{categoryId}")
    public ApiResponse<PaginatedResponse<CourseDTO>> getCoursesByCategory(@PathVariable Integer categoryId, @Valid PaginationRequest paginationRequest) {
        return ApiResponse.paginated(courseRecommendationService.getCoursesByCategory(categoryId, paginationRequest.toPageable()));
    }

    @GetMapping("/{courseId}/similar")
    public ApiResponse<PaginatedResponse<CourseDTO>> getSimilarCourses(@PathVariable Integer courseId, @Valid PaginationRequest paginationRequest) {
        return ApiResponse.paginated(courseRecommendationService.getSimilarCourses(courseId, paginationRequest.toPageable()));
    }

    @GetMapping("/organization/{orgId}")
    public ApiResponse<PaginatedResponse<CourseDTO>> getCoursesByOrganization(
            @PathVariable Integer orgId,
            @Valid PaginationRequest paginationRequest) {
        return ApiResponse.paginated(courseService.getCoursesByOrganization(orgId, paginationRequest.toPageable()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZATION') or hasRole('INSTRUCTOR')")
    public ApiResponse<?> updateCourse(
            @PathVariable Integer id,
            @Valid @RequestBody CourseDTO courseDTO) {

        CourseDTO updatecourse = courseService.updateCourse(id, courseDTO);
        return ApiResponse.success("Course was updated", updatecourse);

    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZATION')")
    public ApiResponse<?> deleteCourse(@PathVariable Integer id) {

        courseService.deleteCourse(id);
        return ApiResponse.noContentResponse();

    }

    @PostMapping("/{courseId}/instructors")
    @PreAuthorize("hasRole('ORGANIZATION')")
    public ApiResponse<?> addInstructorToCourse(
            @PathVariable Integer courseId,
            @RequestBody CourseInstructorDTO courseInstructorDTO,
            @AuthenticationPrincipal User user) throws AccessDeniedException {
        
        CourseDTO courseDTO = instructorService.addInstructorToCourse(
            courseId, 
            courseInstructorDTO.getInstructorId(), 
            user
        );
        return ApiResponse.success(courseDTO);
    }

    @DeleteMapping("/{courseId}/{instructorId}")
    public ApiResponse<?> removeInstructorFromCourse(
            @PathVariable Integer courseId,
            @PathVariable Integer instructorId,
            @AuthenticationPrincipal User user) throws AccessDeniedException {
        
    
        CourseDTO courseDTO = instructorService.removeInstructorFromCourse(
            courseId, 
            instructorId, 
            user
        );
        return ApiResponse.success("the instructor was remove from course",courseDTO);
    }
}
