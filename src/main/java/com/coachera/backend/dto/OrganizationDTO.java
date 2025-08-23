package com.coachera.backend.dto;

import com.coachera.backend.entity.Organization;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Organization Data Transfer Object")
public class OrganizationDTO extends AuditableDTO implements RoleDTO{
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "3")
    private Integer id;

    @NotNull(message = "User ID is required")
    @Schema(required = true, example = "1")
    private Integer userId;

    @Schema(example = "Java Corp")
    private String orgName;

    @Schema(example = "An Organiazation specialized in Java dev.....")
    private String orgDescription;

    public OrganizationDTO(Organization organization) {
        this.id = organization.getId();
        this.userId = organization.getUser().getId(); 
        this.orgName = organization.getOrgName();
        this.orgDescription = organization.getOrgDescription();
        this.setCreatedAt(organization.getCreatedAt());
        this.setUpdatedAt(organization.getUpdatedAt());
    }
}
