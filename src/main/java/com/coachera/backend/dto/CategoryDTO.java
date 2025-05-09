package com.coachera.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CategoryDTO {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "2")
    private Integer id;

    @Schema(example = "Programming")
    private String name;


}
