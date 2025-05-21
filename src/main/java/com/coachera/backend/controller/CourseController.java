package com.coachera.backend.controller;

import com.coachera.backend.dto.ApiResponse;
import com.coachera.backend.dto.CourseDTO;
import com.coachera.backend.entity.Organization;
import com.coachera.backend.entity.User;
import com.coachera.backend.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor

public class CourseController {

    private final CourseService courseService;

    @GetMapping
    public ApiResponse<?> getAllCourses() {

        List<CourseDTO> courses = courseService.getACourses();
        return ApiResponse.success(courses);

    }

    @PostMapping
    @PreAuthorize("hasRole('ORGANIZATION')")
    public ApiResponse<?> createCourse(@Valid @RequestBody CourseDTO courseDTO, @AuthenticationPrincipal User user) {
        CourseDTO createdCourse = courseService.createCourse(courseDTO,user);
        return ApiResponse.created("Course was created", createdCourse);

    }

    @GetMapping("/{id}")
    public ApiResponse<?> getCourse(@PathVariable Integer id) {

        CourseDTO course = courseService.getCourseById(id);
        return ApiResponse.success(course);

    }

    @GetMapping("/organization/{orgId}")
    public ApiResponse<?> getCoursesByOrganization(@PathVariable Integer orgId) {

        List<CourseDTO> courses = courseService.getCoursesByOrganization(orgId);
        return ApiResponse.success("Get all courses by org id", courses);

    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZATION')")
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
        return ApiResponse.noContent();

    }
}