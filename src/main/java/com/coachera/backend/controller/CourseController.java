package com.coachera.backend.controller;

import com.coachera.backend.dto.ApiResponse;
import com.coachera.backend.dto.CourseDTO;
import com.coachera.backend.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor

public class CourseController {

    private final CourseService courseService;

    @GetMapping
    public ApiResponse<?> getAllCourses() {
        try {
            List<CourseDTO> courses = courseService.getACourses();
            return ApiResponse.success(courses);
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ORGANIZATION')")
    public ApiResponse<?> createCourse(@Valid @RequestBody CourseDTO courseDTO) {
        try {
            CourseDTO createdCourse = courseService.createCourse(courseDTO);
            return ApiResponse.created("Course was created", createdCourse);
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<?> getCourse(@PathVariable Integer id) {
        try {
            CourseDTO course = courseService.getCourseById(id);
            return ApiResponse.success(course);
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/organization/{orgId}")
    public ApiResponse<?> getCoursesByOrganization(@PathVariable Integer orgId) {
        try {
            List<CourseDTO> courses = courseService.getCoursesByOrganization(orgId);
            return ApiResponse.success("Get all courses by org id", courses);
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZATION')")
    public ApiResponse<?> updateCourse(
            @PathVariable Integer id,
            @Valid @RequestBody CourseDTO courseDTO) {
        try {
            CourseDTO updatecourse = courseService.updateCourse(id, courseDTO);
            return ApiResponse.success("Course was updated", updatecourse);
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZATION')")
    public ApiResponse<?> deleteCourse(@PathVariable Integer id) {
        try {
            courseService.deleteCourse(id);
            return ApiResponse.noContent();
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}