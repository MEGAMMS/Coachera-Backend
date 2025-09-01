package com.coachera.backend.controller;

import com.coachera.backend.dto.ApiResponse;
import com.coachera.backend.dto.CourseDTO;
import com.coachera.backend.dto.InstructorDTO;
import com.coachera.backend.dto.InstructorRequestDTO;
import com.coachera.backend.dto.pagination.PaginationRequest;
import com.coachera.backend.entity.User;
import com.coachera.backend.service.InstructorService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/instructors")
public class InstructorController {

    private final InstructorService instructorService;

    // @PreAuthorize("hasRole('INSTRUCTOR')")
    @PostMapping
    public ApiResponse<?> createInstructor(@RequestBody @Valid InstructorRequestDTO instructorDTO,
            @AuthenticationPrincipal User user) {
        InstructorDTO createdInstructor = instructorService.createInstructor(instructorDTO, user);
        return ApiResponse.created("Instructor was created successfully", createdInstructor);
    }

    @GetMapping("/me")
    public ApiResponse<?> getCurrentInstructor(@AuthenticationPrincipal User user) {
        InstructorDTO instructor = instructorService.getInstructorByUser(user);
        return ApiResponse.success(instructor);
    }

    @GetMapping
    // @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<?> getInstructors(@Valid PaginationRequest paginationRequest) {
        return ApiResponse.paginated(instructorService.getInstructors(paginationRequest.toPageable()));
    }

    @GetMapping("/no-page")
    public ApiResponse<?> getAllInstructors() {
        return ApiResponse.success(instructorService.getAllInstructors());
    }

    @GetMapping("/{id}")
    public ApiResponse<?> getInstructorById(@PathVariable Integer id) {
        InstructorDTO instructor = instructorService.getInstructorById(id);
        return ApiResponse.success(instructor);
    }

    @PutMapping
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ApiResponse<?> updateInstructor(
            @RequestBody @Valid InstructorRequestDTO requestDTO,
            @AuthenticationPrincipal User user) {
        InstructorDTO updatedInstructor = instructorService.updateInstructor(user, requestDTO);
        return ApiResponse.success("Instructor was updated successfully", updatedInstructor);
    }

    @DeleteMapping
    public ApiResponse<?> deleteInstructor( @AuthenticationPrincipal User user) {
        instructorService.deleteInstructor(user);
        return ApiResponse.noContentResponse();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{instructorId}/courses")
    public ApiResponse<?> getCoursesByInstructorId(
            @PathVariable Integer instructorId) {
        List<CourseDTO> courses = instructorService.getCoursesByInstructorId(instructorId);
        return ApiResponse.success(courses);
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @GetMapping("/courses")
    public ApiResponse<?> getMyCourses(
            @AuthenticationPrincipal User user,
            @Valid PaginationRequest paginationRequest) {
        // List<CourseDTO> courses = instructorService.getMyCourses(user);
        return  ApiResponse.paginated(instructorService.getMyCourses(user,paginationRequest.toPageable()));
    }

    @GetMapping("/courses/{courseId}")
    public ApiResponse<?> getInstructorsByCourseId(
            @PathVariable Integer courseId) {
        List<InstructorDTO> instructors = instructorService.getInstructorsByCourseId(courseId);
        return ApiResponse.success(instructors);
    }

}