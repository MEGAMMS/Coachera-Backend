package com.coachera.backend.entity;

import java.time.LocalDate;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "student_certificates")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StudentCertificate extends Auditable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "certificate_id", nullable = false)
    private Certificate certificate;

    @Column(nullable = false)
    private LocalDate issuedDate;

    // Additional attributes specific to the relationship
    @Column
    private String verificationCode;
    
    @Column
    private Boolean isActive;

    // Composite key constructor
    public StudentCertificate(Student student, Certificate certificate) {
        this.student = student;
        this.certificate = certificate;
        this.issuedDate = LocalDate.now();
        this.isActive = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StudentCertificate)) return false;
        StudentCertificate that = (StudentCertificate) o;
        return Objects.equals(student.getId(), that.student.getId()) &&
               Objects.equals(certificate.getId(), that.certificate.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(student.getId(), certificate.getId());
    }
}
