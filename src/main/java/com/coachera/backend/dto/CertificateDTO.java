package com.coachera.backend.dto;

import java.time.LocalDate;

import com.coachera.backend.entity.Certificate;

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

    // @Schema(example = "2", description = "student who completed the course")
    // private Integer studentId;

    @Schema(required = true, example = "2024-05-08T14:30:00", description = "Timestamp of when the certificate was issued")
    private LocalDate issuedAt;

    @Schema(required = true, example = "https://coachera.com/certificates/123", description = "URL of the certificate")
    private String certificateUrl;

    public CertificateDTO(Certificate certificate) {
        this.id = certificate.getId();
        this.courseId = certificate.getCourse().getId();
        // this.studentId = certificate.getStudent().getId();
        this.issuedAt = certificate.getIssuedAt();
        this.certificateUrl = certificate.getCertificateUrl();
        this.setCreatedAt(certificate.getCreatedAt());
        this.setUpdatedAt(certificate.getUpdatedAt());
    }
}
