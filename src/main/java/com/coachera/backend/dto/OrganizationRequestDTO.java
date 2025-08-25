package com.coachera.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating/updating an Organization.
 * Excludes system-managed fields like id and userId.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Organization Request DTO (for create/update)")
public class OrganizationRequestDTO implements RoleDTO {

    @NotBlank(message = "Organization name is required")
    @Schema(example = "Java Corp", description = "The name of the organization", required = true)
    private String orgName;

    @Schema(example = "An Organization specialized in Java dev.....", 
            description = "Short description of the organization")
    private String orgDescription;
}
