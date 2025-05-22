package com.coachera.backend.dto;

import com.coachera.backend.entity.Instructor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "User Data Transfer Object")
public class InstructorDTO extends AuditableDTO {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "3", description = "Unique identifier of the user")
    private Integer id;

    @Schema(required = true, example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer userId;

    @Schema(example = "A Developer specialized in Java dev.....")
    private String bio;

    public InstructorDTO(Instructor instructor) {
        this.id = instructor.getId();
        this.userId = instructor.getUser() != null ? instructor.getUser().getId() : null;
        this.bio = instructor.getBio();
        this.setCreatedAt(instructor.getCreatedAt());
        this.setUpdatedAt(instructor.getUpdatedAt());
    }
}
