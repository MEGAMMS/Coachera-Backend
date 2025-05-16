package com.coachera.backend.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.coachera.backend.entity.Certificate;
import com.coachera.backend.entity.Student;

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

    // @Schema(example = "[2,3]", description = "students who completed the course")
    // private List<Integer> studentIds;

    @Schema(required = true, example = "2024-05-08T14:30:00", description = "Timestamp of when the certificate was issued")
    private LocalDate issuedAt;

    @Schema(required = true, example = "https://coachera.com/certificates/123", description = "URL of the certificate")
    private String certificateUrl;

    public CertificateDTO(Certificate certificate) {
        this.id = certificate.getId();
        this.courseId = certificate.getCourse().getId();
        //  this.studentIds = certificate.getStudents().stream()
        //         .map(Student::getId)
        //         .collect(Collectors.toList());
        this.issuedAt = certificate.getIssuedAt();
        this.certificateUrl = certificate.getCertificateUrl();
        this.setCreatedAt(certificate.getCreatedAt());
        this.setUpdatedAt(certificate.getUpdatedAt());
    }
}
