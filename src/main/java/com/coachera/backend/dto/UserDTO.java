package com.coachera.backend.dto;

import com.coachera.backend.entity.User;

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

    @Schema(accessMode = Schema.AccessMode.WRITE_ONLY, required = true, example = "strongP@ssword", description = "Password for user authentication")
    private String password;

    @Schema(required = true, example = "user@example.com", description = "User's email address")
    private String email;

    @Schema(required = true, example = "johndoe", description = "Username of the user")
    private String username;

    @Schema(description = "Flag indicating whether the user is verified")
    private Boolean isVerified;

    @Schema(description = "URL to the user's profile image", example = "https://example.com/images/avatar.png")
    private String profileImage;

    @Schema(example = "STUDENT")
    private String role;

    @Schema(example = "http://localhost:8080/images/383980c4-a679-4d8a-a23c-f00ffbf25d76.png")
    private String profileImageUrl;

    public UserDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.isVerified = user.getIsVerified();
        this.profileImage = user.getProfileImage().getUrl();
        this.role = user.getRole();
    }
}
