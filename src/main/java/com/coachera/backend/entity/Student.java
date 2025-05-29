package com.coachera.backend.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<StudentCertificate> studentCertificates = new HashSet<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<StudentSkill> studentSkills = new HashSet<>();

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

    // Additional student fields
    @Column
    private String phoneNumber;

    @Column
    private String address;

    // Helper methods
    public void addCertificate(Certificate certificate) {
        StudentCertificate studentCertificate = new StudentCertificate(this, certificate);
        studentCertificates.add(studentCertificate);
        certificate.getStudents().add(studentCertificate);
    }

    public void removeCertificate(Certificate certificate) {
        StudentCertificate studentCertificate = new StudentCertificate(this, certificate);
        certificate.getStudents().remove(studentCertificate);
        studentCertificates.remove(studentCertificate);
    }

    // Convenience method to get certificates
    public Set<Certificate> getCertificates() {
        return studentCertificates.stream()
                .map(StudentCertificate::getCertificate)
                .collect(Collectors.toSet());
    }

    public void addSkill(Skill skill, Course course, Integer level) {
        StudentSkill studentSkill = new StudentSkill();
        studentSkill.setStudent(this);
        studentSkill.setSkill(skill);
        studentSkill.setCourse(course);
        studentSkill.setLevel(level);
        studentSkills.add(studentSkill);
        skill.getStudentSkills().add(studentSkill);
    }

    public void removeSkill(Skill skill) {
        StudentSkill studentSkill = new StudentSkill();
        studentSkill.setStudent(this);
        studentSkill.setSkill(skill);
        skill.getStudentSkills().remove(studentSkill);
        studentSkills.remove(studentSkill);
    }

    public Set<Skill> getSkills() {
        return studentSkills.stream()
                .map(StudentSkill::getSkill)
                .collect(Collectors.toSet());
    }

    // Helper method to maintain bidirectional relationship
    public void setUser(User user) {
        this.user = user;
        if (user != null && user.getStudent() != this) {
            user.setStudent(this);
        }
    }
}
