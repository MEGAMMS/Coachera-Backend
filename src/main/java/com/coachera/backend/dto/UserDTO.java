package com.coachera.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserDTO {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "1")
    private Integer id;

    @Schema(example = "teto75928@gmail.com")
    private String email;

    @Schema(example = "tARiqTetonaroto")
    private String password;

}
