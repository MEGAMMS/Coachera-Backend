package com.coachera.backend.entity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "certificates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Certificate extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @OneToMany(mappedBy = "certificate", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<StudentCertificate> students = new HashSet<>();

    @Column(nullable = false)
    private LocalDate issuedAt;

    @Column(nullable = false)
    private String certificateUrl;

    // Additional certificate fields
    @Column
    private String certificateNumber;

    @Column
    private Integer validityYears;

    // Helper methods
    public void addStudent(Student student) {
        StudentCertificate studentCertificate = new StudentCertificate(student, this);
        if(students==null){
            students = new HashSet<>();
        }
        students.add(studentCertificate);
        student.getStudentCertificates().add(studentCertificate);
    }

    public void removeStudent(Student student) {
        StudentCertificate studentCertificate = new StudentCertificate(student, this);
        student.getStudentCertificates().remove(studentCertificate);
        students.remove(studentCertificate);
    }
}
