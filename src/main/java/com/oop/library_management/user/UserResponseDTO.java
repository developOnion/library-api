package com.oop.library_management.user;

public record UserResponseDTO(

	Long id,
	String username,
	String firstName,
	String lastName,
	Role role,
	String displayInfo
) {
}
