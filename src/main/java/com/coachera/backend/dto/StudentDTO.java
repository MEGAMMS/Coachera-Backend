package com.coachera.backend.dto;

import java.time.LocalDate;

import com.coachera.backend.entity.User;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StudentDTO {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "3")
    private Integer id;

    @Schema(example = "Tariq")
    private String firstName;

    @Schema(example="sadooo")
    private String lastName;

    @Schema(example = "27-07-2004")
    private LocalDate birthDate;

    @Schema(example = "male")
    private String gender;

    @Schema(example = "ITE_third_year")
    private String education;

    @Schema(example = "1")
    private Integer userId;

}
