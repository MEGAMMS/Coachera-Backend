package com.coachera.backend.controller;

import com.coachera.backend.dto.EnrollmentDTO;
import com.coachera.backend.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
@Tag(name = "Enrollments", description = "Endpoints for managing student course enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get all enrollments for a student")
    public ResponseEntity<List<EnrollmentDTO>> getEnrollmentsByStudentId(@PathVariable Integer studentId) {
        List<EnrollmentDTO> enrollments = enrollmentService.getEnrollmentsByStudentId(studentId);
        return ResponseEntity.ok(enrollments);
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "Get all enrollments for a course")
    public ResponseEntity<List<EnrollmentDTO>> getEnrollmentsByCourseId(@PathVariable Integer courseId) {
        List<EnrollmentDTO> enrollments = enrollmentService.getEnrollmentsByCourseId(courseId);
        return ResponseEntity.ok(enrollments);
    }

    @PostMapping("/{courseId}/{studentId}")
    @Operation(summary = "Enroll a student in a course")
    public ResponseEntity<EnrollmentDTO> enrollStudent(
            @PathVariable Integer studentId,
            @PathVariable Integer courseId,
            @RequestParam(defaultValue = "0%")  String progress) {
        EnrollmentDTO enrollmentDTO = enrollmentService.enrollStudent(studentId, courseId, progress);
        return ResponseEntity.status(HttpStatus.CREATED).body(enrollmentDTO);
    }

    @PutMapping("/progress")
    @Operation(summary = "Update student's progress in a course")
    public ResponseEntity<EnrollmentDTO> updateProgress(
            @RequestBody Integer studentId,
            @RequestBody Integer courseId,
            @RequestParam(defaultValue = "0%")  String progress) {
        EnrollmentDTO enrollmentDTO = enrollmentService.updateProgress(studentId, courseId, progress);
        return ResponseEntity.ok(enrollmentDTO);
    }

    @DeleteMapping("/delete/{courseId}/{studentId}")
    @Operation(summary = "Unenroll a student from a course")
    public ResponseEntity<Void> unenrollStudent(
            @PathVariable Integer studentId,
            @PathVariable Integer courseId) {
        enrollmentService.unenrollStudent(studentId, courseId);
        return ResponseEntity.noContent().build();
    }
}