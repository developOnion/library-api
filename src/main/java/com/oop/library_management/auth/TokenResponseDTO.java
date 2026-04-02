package com.oop.library_management.auth;

public record TokenResponseDTO(
	String accessToken,
	String refreshToken
) {
}