package com.oop.library_management.auth;

import com.oop.library_management.user.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication")
public class AuthController {

	private final UserService userService;

	public AuthController(
		UserService userService
	) {

		this.userService = userService;
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponseDTO> login(
		@Valid @RequestBody AuthRequestDTO loginRequest
	) {

		AuthResponseDTO token = userService.authenticate(loginRequest);

		return ResponseEntity.ok().body(token);
	}
}
