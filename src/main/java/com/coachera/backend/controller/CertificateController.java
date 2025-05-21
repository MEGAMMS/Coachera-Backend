package com.coachera.backend.controller;

import com.coachera.backend.dto.ApiResponse;
import com.coachera.backend.dto.CertificateDTO;
import com.coachera.backend.service.CertificateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Set;

@RestController
@RequestMapping("/api/certificates")
@PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZATION')")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;

    @PostMapping
    public ApiResponse<?> createCertificate(
            @Valid @RequestBody CertificateDTO certificateDTO) {
        CertificateDTO createdCert = certificateService.createCertificate(certificateDTO);
        return ApiResponse.created("Certificate was created", createdCert);
    }

  

    @GetMapping("/{id}")
    public ApiResponse<?> getCertificate(@PathVariable Integer id) {
        return ApiResponse.success(certificateService.getCertificateById(id));
    }

    @PutMapping("/{id}")
    public ApiResponse<?> updateCertificate(
            @PathVariable Integer id,
            @Valid @RequestBody CertificateDTO certificateDTO) {
        return ApiResponse.success(certificateService.updateCertificate(id, certificateDTO));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<?> deleteCertificate(@PathVariable Integer id) {
        certificateService.deleteCertificate(id);
        return ApiResponse.noContent();
    }

  

    @GetMapping("/course/{courseId}")
    public ApiResponse<?> getCertificatesByCourse(
            @PathVariable Integer courseId) {
        return ApiResponse.success(certificateService.getCertificatesByCourse(courseId));
    }

   

    @DeleteMapping("/{certificateId}/students/{studentId}")
    public ApiResponse<?> removeStudentFromCertificate(
            @PathVariable Integer certificateId,
            @PathVariable Integer studentId) {
        certificateService.removeStudentFromCertificate(certificateId, studentId);
        return ApiResponse.noContent();
    }

    @GetMapping("/{certificateId}/students")
    public ApiResponse<?> getCertificateStudents(
            @PathVariable Integer certificateId) {
        return ApiResponse.success(certificateService.getStudentsByCertificateId(certificateId));
    }

     @PostMapping("/{certificateId}/students")
    public ApiResponse<?> addStudentsToCertificate(
            @PathVariable Integer certificateId,
            @RequestBody Set<Integer> studentIds) {
        return ApiResponse.success(certificateService.addStudents(certificateId, studentIds));
            }

    @PatchMapping("/{id}/issued-date")
    public ApiResponse<?> updateIssuedDate(
            @PathVariable Integer id,
            @RequestParam LocalDate newDate) {
        return ApiResponse.success(certificateService.updateIssuedDate(id, newDate));
    }

    // @GetMapping("/student/{studentId}/course/{courseId}")
    // public ResponseEntity<CertificateDTO> getCertificateByStudentAndCourse(
    //         @PathVariable Integer studentId,
    //         @PathVariable Integer courseId) {
    //     return ResponseEntity.ok(
    //             certificateService.getCertificateByStudentAndCourse(studentId, courseId));
    // }
}