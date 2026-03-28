package com.oop.library_management.author;

import com.oop.library_management.validation.atleastonename.AtLeastOneName;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@AtLeastOneName
public record AuthorRequestDTO(

	@Size(max = 50, message = "First name must be at most 50 characters")
	@Pattern(regexp = "^\\S+$", message = "First name cannot contain whitespace")
	String firstName,

	@Size(max = 50, message = "Last name must be at most 50 characters")
	@Pattern(regexp = "^\\S+$", message = "Last name cannot contain whitespace")
	String lastName,

	@NotNull(message = "Author type is required")
	AuthorType type
) {
}
