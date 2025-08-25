package com.coachera.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Request DTO for creating/updating a Student profile.
 * Excludes system-managed fields such as id, userId, wallet, and audit fields.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Student Request DTO (for create/update)")
public class StudentRequestDTO implements RoleDTO{

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

    @Schema(example = "+1234567890", description = "Phone number")
    private String phoneNumber;

    @Schema(example = "123 Main St, City", description = "Physical address")
    private String address;
}
