package com.coachera.backend.dto;

import com.coachera.backend.entity.Organization;

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

    public OrganizationDTO(Organization organization) {
        this.id = organization.getId();
        this.user = new UserDTO(organization.getUser()); // assumes UserDTO has a constructor taking User
        this.orgName = organization.getOrgName();
        this.orgDescription = organization.getOrgDescription();
        this.setCreatedAt(organization.getCreatedAt());
        this.setUpdatedAt(organization.getUpdatedAt());
    }
}
