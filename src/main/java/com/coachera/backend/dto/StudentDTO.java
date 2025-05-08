package com.coachera.backend.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "3", description = "Unique identifier of the student")
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserDTO user;

    private String firstName;

    private String lastName;

    private LocalDate birthDate;

    private String gender;

    private String education;
}
