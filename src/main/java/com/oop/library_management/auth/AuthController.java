package com.oop.library_management.auth;

import com.oop.library_management.security.JwtService;
import com.oop.library_management.user.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication")
public class AuthController {

	private final UserService userService;
	private final JwtService jwtService;

	public AuthController(
		UserService userService,
		JwtService jwtService
	) {

		this.userService = userService;
		this.jwtService = jwtService;
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponseDTO> login(
		@Valid @RequestBody AuthRequestDTO loginRequest,
		HttpServletResponse response
	) {

		UserService.TokenResponse tokenResponse = userService.authenticate(loginRequest);

		attachRefreshTokenCookie(response, tokenResponse.refreshToken());

		return ResponseEntity.ok()
			.body(new AuthResponseDTO(tokenResponse.accessToken()));
	}

	@PostMapping("/refresh-token")
	public ResponseEntity<AuthResponseDTO> refreshToken(
		@CookieValue(name = "refresh_token") String refreshToken,
		HttpServletResponse response
	) {
		UserService.TokenResponse tokenResponse = userService.refreshToken(refreshToken);

		attachRefreshTokenCookie(response, tokenResponse.refreshToken());

		return ResponseEntity.ok()
			.body(new AuthResponseDTO(tokenResponse.accessToken()));
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(HttpServletResponse response, java.security.Principal principal) {

		if (principal != null) {
			userService.revokeAllTokens(principal.getName());
		}

		ResponseCookie cookie = ResponseCookie.from("refresh_token", "")
			.httpOnly(true)
			.secure(false)
			.path("/api/v1/auth/refresh-token")
			.maxAge(0) // Immediately expire
			.sameSite("Strict")
			.build();

		response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

		return ResponseEntity.ok().build();
	}

	private void attachRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
		ResponseCookie cookie = jwtService.createRefreshTokenCookie(refreshToken);
		response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
	}
}
