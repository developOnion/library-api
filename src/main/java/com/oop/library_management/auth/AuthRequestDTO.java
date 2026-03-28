package com.oop.library_management.auth;

import jakarta.validation.constraints.NotBlank;

public record AuthRequestDTO(
	@NotBlank(message = "Username is required")
	String username,

	@NotBlank(message = "Password is required")
	String password
) {
}
