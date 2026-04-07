package com.oop.library_management.token;

public record TokenResponseDTO(
	String accessToken,
	String refreshToken
) {
}