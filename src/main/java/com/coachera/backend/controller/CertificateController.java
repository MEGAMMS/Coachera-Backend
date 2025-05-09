package com.coachera.backend.controller;

import com.coachera.backend.dto.CertificateDTO;
import com.coachera.backend.service.CertificateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/certificates")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;

    @PostMapping
    public ResponseEntity<CertificateDTO> createCertificate(
            @Valid @RequestBody CertificateDTO certificateDTO) {
        CertificateDTO createdCert = certificateService.createCertificate(certificateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCert);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CertificateDTO> getCertificate(@PathVariable Integer id) {
        return ResponseEntity.ok(certificateService.getCertificateById(id));
    }

    // @GetMapping("/student/{studentId}")
    // public ResponseEntity<List<CertificateDTO>> getByStudent(
    //         @PathVariable Integer studentId) {
    //     return ResponseEntity.ok(certificateService.getCertificatesByStudent(studentId));
    // }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<CertificateDTO>> getByCourse(
            @PathVariable Integer courseId) {
        return ResponseEntity.ok(certificateService.getCertificatesByCourse(courseId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCertificate(@PathVariable Integer id) {
        certificateService.deleteCertificate(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
public ResponseEntity<CertificateDTO> updateCertificate(
        @PathVariable Integer id,
        @Valid @RequestBody CertificateDTO certificateDTO) {
    CertificateDTO updatedCert = certificateService.updateCertificate(id, certificateDTO);
    return ResponseEntity.ok(updatedCert);
}
}