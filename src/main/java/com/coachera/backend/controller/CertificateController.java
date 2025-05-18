package com.coachera.backend.controller;

import com.coachera.backend.dto.CertificateDTO;
import com.coachera.backend.dto.StudentDTO;
import com.coachera.backend.service.CertificateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/certificates")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;

    @PostMapping
    public ResponseEntity<CertificateDTO> createCertificate(
            @Valid @RequestBody CertificateDTO certificateDTO) {
        CertificateDTO createdCert = certificateService.createCertificate(certificateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCert);
    }

    @GetMapping
    public ResponseEntity<List<CertificateDTO>> getAllCertificates() {
        return ResponseEntity.ok(certificateService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CertificateDTO> getCertificate(@PathVariable Integer id) {
        return ResponseEntity.ok(certificateService.getCertificateById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CertificateDTO> updateCertificate(
            @PathVariable Integer id,
            @Valid @RequestBody CertificateDTO certificateDTO) {
        return ResponseEntity.ok(certificateService.updateCertificate(id, certificateDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCertificate(@PathVariable Integer id) {
        certificateService.deleteCertificate(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<CertificateDTO>> getCertificatesByStudent(
            @PathVariable Integer studentId) {
        return ResponseEntity.ok(certificateService.getCertificatesByStudent(studentId));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<CertificateDTO>> getCertificatesByCourse(
            @PathVariable Integer courseId) {
        return ResponseEntity.ok(certificateService.getCertificatesByCourse(courseId));
    }

    @PostMapping("/{certificateId}/students")
    public ResponseEntity<CertificateDTO> addStudentsToCertificate(
            @PathVariable Integer certificateId,
            @RequestBody Set<Integer> studentIds) {
        return ResponseEntity.ok(
                certificateService.addStudents(certificateId, studentIds));
    }

    @DeleteMapping("/{certificateId}/students/{studentId}")
    public ResponseEntity<Void> removeStudentFromCertificate(
            @PathVariable Integer certificateId,
            @PathVariable Integer studentId) {
        certificateService.removeStudentFromCertificate(certificateId, studentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{certificateId}/students")
    public ResponseEntity<List<StudentDTO>> getCertificateStudents(
            @PathVariable Integer certificateId) {
        return ResponseEntity.ok(
                certificateService.getStudentsByCertificateId(certificateId));
    }

    @PatchMapping("/{id}/issued-date")
    public ResponseEntity<CertificateDTO> updateIssuedDate(
            @PathVariable Integer id,
            @RequestParam LocalDate newDate) {
        return ResponseEntity.ok(
                certificateService.updateIssuedDate(id, newDate));
    }

    // @GetMapping("/student/{studentId}/course/{courseId}")
    // public ResponseEntity<CertificateDTO> getCertificateByStudentAndCourse(
    //         @PathVariable Integer studentId,
    //         @PathVariable Integer courseId) {
    //     return ResponseEntity.ok(
    //             certificateService.getCertificateByStudentAndCourse(studentId, courseId));
    // }
}