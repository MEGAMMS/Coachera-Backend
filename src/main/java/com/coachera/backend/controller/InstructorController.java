package com.coachera.backend.controller;

import com.coachera.backend.dto.ApiResponse;
import com.coachera.backend.dto.CourseDTO;
import com.coachera.backend.dto.InstructorDTO;
import com.coachera.backend.dto.pagination.PaginationRequest;
import com.coachera.backend.entity.User;
import com.coachera.backend.service.InstructorService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/instructors")
public class InstructorController {

    private final InstructorService instructorService;

    public InstructorController(InstructorService instructorService) {
        this.instructorService = instructorService;
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @PostMapping
    public ApiResponse<?> createInstructor(@RequestBody @Valid InstructorDTO instructorDTO,
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
    public ApiResponse<?> getAllInstructors(@Valid PaginationRequest paginationRequest) {
        return ApiResponse.paginated(instructorService.getInstructors(paginationRequest.toPageable()));
    }

    @GetMapping("/{id}")
    public ApiResponse<?> getInstructorById(@PathVariable Integer id) {
        InstructorDTO instructor = instructorService.getInstructorById(id);
        return ApiResponse.success(instructor);
    }

    @PutMapping
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ApiResponse<?> updateInstructor(
            @RequestBody @Valid InstructorDTO instructorDTO,
            @AuthenticationPrincipal User user) {
        InstructorDTO updatedInstructor = instructorService.updateInstructor(user, instructorDTO);
        return ApiResponse.success("Instructor was updated successfully",updatedInstructor);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<?> deleteInstructor(@PathVariable Integer id) {
        instructorService.deleteInstructor(id);
        return ApiResponse.noContentResponse();
    }

      @GetMapping("/{instructorId}/courses")
    public ApiResponse<?> getCoursesByInstructorId(
            @PathVariable Integer instructorId) {
        List<CourseDTO> courses = instructorService.getCoursesByInstructorId(instructorId);
        return ApiResponse.success(courses);
    }

    @GetMapping("/courses/{courseId}")
    public ApiResponse<?> getInstructorsByCourseId(
            @PathVariable Integer courseId) {
        List<InstructorDTO> instructors = instructorService.getInstructorsByCourseId(courseId);
        return ApiResponse.success(instructors);
    }


}