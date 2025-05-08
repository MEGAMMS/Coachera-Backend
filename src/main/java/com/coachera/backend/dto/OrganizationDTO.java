package com.coachera.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class OrganizationDTO {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "3")
    private Integer id;

    // private UserDTO user;

    @Schema(example = "Java Corp")
    private String orgName;

    @Schema(example = "An Organiazation specialized in Java dev.....")
    private String orgDescription;
}
