package com.oop.library_management.category;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequestDTO(
	@NotBlank(message = "Category name is required")
	String name
) {
}
