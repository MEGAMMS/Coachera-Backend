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

    @Schema(description = "Associated user information")
    private UserDTO user;

    @Schema(required = true, example = "john", description = "firstname of the student")
    private String firstName;

    @Schema(required = true, example = "doe", description = "lastname of the student")
    private String lastName;

    @Schema(required = true, example = "2024-05-08T14:30:00", description = "dude's birthdate")
    private LocalDate birthDate;

    @Schema(required = true, example = "male")
    private String gender;
    
    @Schema(required = true, example = "Bachelor", description = "level of education")
    private String education;
}
