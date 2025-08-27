package com.coachera.backend.dto;

import com.coachera.backend.entity.Student;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
// import java.util.Set;
// import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Student Data Transfer Object")
public class StudentDTO extends AuditableDTO implements RoleDTO {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "1")
    private Integer id;

    @Schema(example = "1", description = "Associated user ID", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer userId;

    @Schema(example = "John", description = "First name", required = true)
    private String firstName;

    @Schema(example = "Doe", description = "Last name", required = true)
    private String lastName;

    @Schema(example = "1990-01-15", description = "Date of birth (YYYY-MM-DD)", required = true)
    private LocalDate birthDate;

    @Schema(example = "MALE", description = "Gender", allowableValues = {"MALE", "FEMALE", "OTHER"}, required = true)
    private String gender;

    @Schema(example = "Bachelor's Degree", description = "Education level", required = true)
    private String education;

    @Schema(example = "100.50", description = "Wallet balance", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal wallet;

    @Schema(example = "+1234567890", description = "Phone number")
    private String phoneNumber;

    @Schema(example = "123 Main St, City", description = "Physical address")
    private String address;

    // @Schema(description = "Certificate IDs associated with the student", accessMode = Schema.AccessMode.READ_ONLY)
    // private Set<Integer> certificateIds;

    // @Schema(description = "Skills possessed by the student", accessMode = Schema.AccessMode.READ_ONLY)
    // private Set<StudentSkillDTO> skills;

    public StudentDTO(Student student) {
        this.id = student.getId();
        this.userId = student.getUser() != null ? student.getUser().getId() : null;
        this.firstName = student.getFirstName();
        this.lastName = student.getLastName();
        this.birthDate = student.getBirthDate();
        this.gender = student.getGender();
        this.education = student.getEducation();
        this.wallet = student.getWallet();
        this.phoneNumber = student.getPhoneNumber();
        this.address = student.getAddress();
        // this.certificateIds = student.getStudentCertificates().stream()
        //         .map(sc -> sc.getCertificate().getId())
        //         .collect(Collectors.toSet());
        // this.skills = student.getStudentSkills().stream()
        //         .map(StudentSkillDTO::new)
        //         .collect(Collectors.toSet());
        this.setCreatedAt(student.getCreatedAt());
        this.setUpdatedAt(student.getUpdatedAt());
    }
}
