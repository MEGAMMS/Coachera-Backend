package com.coachera.backend.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CourseDTO {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "3")
    private Integer id;

    @Schema(example = "Java totorial")
    private String title;

    @Schema(example = "hello guys and welcome back.....")
    private String description;

    // private Integer orgId;
}
