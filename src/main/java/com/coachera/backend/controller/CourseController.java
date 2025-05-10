package com.coachera.backend.controller;

import com.coachera.backend.dto.CourseDTO;
import com.coachera.backend.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<List<CourseDTO>> getAllCourses() {
        return new ResponseEntity<>(courseService.getACourses(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CourseDTO> createCourse(
            @Valid @RequestBody CourseDTO courseDTO) {
        CourseDTO createdCourse = courseService.createCourse(courseDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCourse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getCourse(@PathVariable Integer id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @GetMapping("/organization/{orgId}")
    public ResponseEntity<List<CourseDTO>> getCoursesByOrganization(
            @PathVariable Integer orgId) {
        return ResponseEntity.ok(courseService.getCoursesByOrganization(orgId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseDTO> updateCourse(
            @PathVariable Integer id,
            @Valid @RequestBody CourseDTO courseDTO) {
        return ResponseEntity.ok(courseService.updateCourse(id, courseDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Integer id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}