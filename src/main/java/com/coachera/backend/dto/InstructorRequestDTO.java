package com.coachera.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating/updating an Instructor.
 * Excludes IDs since they are managed by the system.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Instructor Request DTO (for create/update)")
public class InstructorRequestDTO implements RoleDTO{

    @Schema(example = "A Developer specialized in Java dev.....", 
            description = "Short biography or specialization details of the instructor",
            required = true)
    private String bio;
    
    @Schema(example = "Muhannad Wahbeh")
    private String name;
}
