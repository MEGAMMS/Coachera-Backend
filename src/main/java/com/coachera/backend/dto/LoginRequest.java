package com.coachera.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
	@Schema(example = "test@gmail.com")
	private String identifier; // Can be username or email
	@Schema(example = "password")
	private String password;
}
