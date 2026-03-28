package com.oop.library_management.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthResponseDTO(
	@JsonProperty("access_token")
	String accessToken
) {
}
