package com.oop.library_management.user;

import com.oop.library_management.validation.validationgroup.CreateLibrarianValidation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRequestDTO(

	@NotBlank(message = "Username is required")
	@Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
	String username,

	@NotBlank(message = "Password is required")
	@Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
	String password,

	@NotBlank(message = "First name is required")
	@Size(max = 50, message = "First name must be at most 50 characters")
	String firstName,

	@NotBlank(message = "Last name is required")
	@Size(max = 50, message = "Last name must be at most 50 characters")
	String lastName,

	@NotNull(
		groups = CreateLibrarianValidation.class,
		message = "Position is required for librarian"
	)
	LibrarianPosition position
) {
}
