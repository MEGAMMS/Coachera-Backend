package com.coachera.backend.dto;

import com.coachera.backend.entity.User;
import com.coachera.backend.entity.enums.RoleType;

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
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "1")
    private Integer id;

    @Schema(example = "user@example.com", description = "Unique email address", required = true)
    private String email;

    @Schema(example = "john_doe", description = "Unique username", required = true)
    private String username;

    @Schema(example = "STUDENT", description = "User role", 
             allowableValues = {"ADMIN", "INSTRUCTOR", "STUDENT","ORGANIZATION"}, required = true)
    private RoleType role;

    @Schema(description = "Profile image details")
    private String profileImage;

    @Schema(example = "true", description = "Whether the user is verified")
    private Boolean isVerified;

    @Schema(description = "Role-specific details (Student, Instructor, Organization, etc.)")
    private RoleDTO details;  // <-- New polymorphic field

    public UserDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.role = user.getRole();
        this.isVerified = user.getIsVerified();
        if (user.getProfileImage() != null) {
            this.profileImage = user.getProfileImage().getUrl();
        }
        this.details = user.getRoleDetails();
        this.setCreatedAt(user.getCreatedAt());
        this.setUpdatedAt(user.getUpdatedAt());
    }

    // Security-conscious version that excludes sensitive/optional fields
    public static UserDTO safeUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole());
        if (user.getProfileImage() != null) {
            dto.setProfileImage(user.getProfileImage().getUuidName());
        }
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
}
