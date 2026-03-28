package com.oop.library_management.book;

import jakarta.validation.constraints.*;

import java.util.Set;

public record BookRequestDTO(

	@NotBlank(message = "Title is required")
	@Size(max = 255, message = "Title must be at most 255 characters")
	String title,

	@Size(max = 20, message = "ISBN must be at most 20 characters")
	String isbn,

	@NotNull(message = "Total copies is required")
	@Min(value = 1, message = "Total copies must be at least 1")
	Integer totalCopies,

	@NotEmpty(message = "At least one author is required")
	Set<Long> authorIds,

	@NotEmpty(message = "At least one category is required")
	Set<Long> categoryIds
) {
}
