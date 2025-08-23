package com.coachera.backend.dto;

import com.coachera.backend.entity.enums.RoleType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
	@Schema(example = "student")
	private String username;

	@Schema(example = "student@gmail.com")
	private String email;

	@Schema(example = "password")
	private String password;

	@Schema(example = "student")
	@NotBlank(message = "Role is required")
	private RoleType role;

	@Schema(example = "http://localhost:8080/images/383980c4-a679-4d8a-a23c-f00ffbf25d76.png")
	private String profileImageUrl;

	private RoleDTO details;
}
