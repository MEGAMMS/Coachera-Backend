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
@Schema(description = "Course Data Transfer Object")
public class CourseDTO extends AuditableDTO {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "3")
    private Integer id;

    @Schema(example = "Java totorial")
    private String title;

    @Schema(example = "hello guys and welcome back.....")
    private String description;

    @Schema(description = "Associated org information")
    private OrganizationDTO org;
}
