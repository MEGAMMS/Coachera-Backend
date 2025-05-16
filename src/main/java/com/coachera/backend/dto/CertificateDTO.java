package com.coachera.backend.dto;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import com.coachera.backend.entity.Certificate;
import com.coachera.backend.entity.StudentCertificate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Certificate Data Transfer Object")
public class CertificateDTO extends AuditableDTO {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "3", description = "Unique identifier of the certificate")
    private Integer id;

    @Schema(required = true, example = "1", description = "Completed Course")
    private Integer courseId;

    @Schema(example = "[2,3]", description = "Students who received this certificate")
    private Set<Integer> studentIds;

    @Schema(example = "CERT-12345", description = "Certificate serial number")
    private String certificateNumber;

    @Schema(example = "5", description = "Number of years the certificate is valid")
    private Integer validityYears;

    @Schema(required = true, example = "2024-05-08", description = "Date when the certificate was issued")
    private LocalDate issuedAt;

    @Schema(required = true, example = "https://coachera.com/certificates/123", description = "URL of the certificate")
    private String certificateUrl;

    public CertificateDTO(Certificate certificate) {
        this.id = certificate.getId();
        this.courseId = certificate.getCourse().getId();
        this.studentIds = certificate.getStudentCertificates().stream()
                .map(sc -> sc.getStudent().getId())
                .collect(Collectors.toSet());
        this.certificateNumber = certificate.getCertificateNumber();
        this.validityYears = certificate.getValidityYears();
        this.issuedAt = certificate.getIssuedAt();
        this.certificateUrl = certificate.getCertificateUrl();
        this.setCreatedAt(certificate.getCreatedAt());
        this.setUpdatedAt(certificate.getUpdatedAt());
    }
}