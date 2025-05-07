package com.coachera.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
}
