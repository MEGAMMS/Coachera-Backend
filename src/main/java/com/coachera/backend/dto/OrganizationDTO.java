package com.coachera.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Organization Data Transfer Object")
public class OrganizationDTO extends AuditableDTO {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "3")
    private Integer id;

    @Schema(description = "Associated user information")
    private UserDTO user;

    @Schema(example = "Java Corp")
    private String orgName;

    @Schema(example = "An Organiazation specialized in Java dev.....")
    private String orgDescription;
}
