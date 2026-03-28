package com.oop.library_management.author;

public record AuthorResponseDTO(
	Long id,
	String fullName,
	String firstName,
	String lastName,
	AuthorType type
) {
}
