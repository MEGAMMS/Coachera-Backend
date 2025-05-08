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
@Schema(description = "User Data Transfer Object")
public class UserDTO extends AuditableDTO {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "3", description = "Unique identifier of the user")
    private Integer id;

    @Schema(required = true, example = "strongP@ssword", description = "Password for user authentication")
    private String password;

    @Schema(required = true, example = "user@example.com", description = "User's email address")
    private String email;

    @Schema(required = true, example = "johndoe", description = "Username of the user")
    private String username;

    @Schema(description = "Flag indicating whether the user is verified")
    private Boolean isVerified;

    @Schema(description = "URL to the user's profile image", example = "https://example.com/images/avatar.png")
    private String profileImage;
}
