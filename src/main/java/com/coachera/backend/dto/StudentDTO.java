package com.coachera.backend.dto;

import java.time.LocalDate;

import com.coachera.backend.entity.Student;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Student Data Transfer Object")
public class StudentDTO extends AuditableDTO {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Integer id;

    @NotNull(message = "User ID is required")
    @Schema(required = true, example = "1")
    private Integer userId;

    @NotBlank(message = "First name is required")
    @Schema(required = true, example = "John")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Schema(required = true, example = "Doe")
    private String lastName;

    @NotNull(message = "Birth date is required")
    @Schema(required = true, example = "2000-01-01")
    private LocalDate birthDate;

    @NotBlank(message = "Gender is required")
    @Schema(required = true, example = "male")
    private String gender;

    @NotBlank(message = "Education is required")
    @Schema(required = true, example = "Bachelor")
    private String education;

    public StudentDTO(Student student) {
        this.id = student.getId();
        this.userId = student.getUser() != null ? student.getUser().getId() : null;
        this.firstName = student.getFirstName();
        this.lastName = student.getLastName();
        this.birthDate = student.getBirthDate();
        this.gender = student.getGender();
        this.education = student.getEducation();
        this.setCreatedAt(student.getCreatedAt());
        this.setUpdatedAt(student.getUpdatedAt());
    }
}
