package com.coachera.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
	@Schema(example = "test")
	private String username;

	@Schema(example = "test@gmail.com")
	private String email;

	@Schema(example = "password")
	private String password;

	@Schema(example = "student")
	@NotBlank(message = "Role is required")
	private String role;

	@Schema(example = "http://localhost:8080/images/383980c4-a679-4d8a-a23c-f00ffbf25d76.png")
	private String profileImageUrl;
}
