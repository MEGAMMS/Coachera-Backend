package com.coachera.backend.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "students")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Student extends Auditable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StudentCertificate> studentCertificates = new HashSet<>();

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false)
    private String education;

    @Column(nullable = false)
    private BigDecimal wallet;

    // // Additional student fields
    // @Column
    // private String phoneNumber;
    
    // @Column
    // private String address;

    // Helper methods
    public void addCertificate(Certificate certificate) {
        StudentCertificate studentCertificate = new StudentCertificate(this, certificate);
        studentCertificates.add(studentCertificate);
        certificate.getStudentCertificates().add(studentCertificate);
    }

    public void removeCertificate(Certificate certificate) {
        StudentCertificate studentCertificate = new StudentCertificate(this, certificate);
        certificate.getStudentCertificates().remove(studentCertificate);
        studentCertificates.remove(studentCertificate);
    }

    // Convenience method to get certificates
    public Set<Certificate> getCertificates() {
        return studentCertificates.stream()
                .map(StudentCertificate::getCertificate)
                .collect(Collectors.toSet());
    }
}

