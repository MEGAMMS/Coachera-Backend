package com.coachera.backend.dto;

import java.time.LocalDate;

import org.springframework.shell.standard.ShellCommandGroup;

import com.coachera.backend.entity.Course;
import com.coachera.backend.entity.Student;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor @NoArgsConstructor
@Schema(description = "Certificate Data Transfer Object")
public class CertificateDTO extends AuditableDTO {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "3", description = "Unique identifier of the certificate")
    private Integer id;

    @Schema(required = true, example = "Introduction to OOP", description = "Completed Course")
    private CourseDTO course;

    //@Schema(required = true, example = "John the baptist", description = "student who completed the course")
    //private StudentDTO student;

    @Schema(required = true, example = "2024-05-08T14:30:00", description = "Timestamp of when the certificate was issued")
    private LocalDate issuedAt;

    @Schema(required = true, example = "https://coachera.com/certificates/123", description = "URL of the certificate")
    private String certificateUrl;
}
