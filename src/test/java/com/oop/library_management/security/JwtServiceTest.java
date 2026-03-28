package com.oop.library_management.security;

import com.oop.library_management.user.Role;
import com.oop.library_management.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

	private JwtService jwtService;

	@BeforeEach
	void setUp() {
		jwtService = new JwtService();
		ReflectionTestUtils.setField(jwtService, "SECRET_KEY", "my_super_secret_key_that_is_at_least_32_bytes_long_for_hmac");
		ReflectionTestUtils.setField(jwtService, "ACCESS_TOKEN_EXPIRATION", 1000L * 60 * 15);
		ReflectionTestUtils.setField(jwtService, "REFRESH_TOKEN_EXPIRATION", 1000L * 60 * 60 * 24 * 7);
	}

	@Test
	void testGenerateAndValidateToken() {
		String username = "testuser";
		Role role = Role.MEMBER;

		String token = jwtService.generateAccessToken(username, role);

		User user = new User() {
			@Override
			public String getDisplayInfo() { return null; }
		};
		user.setUsername(username);

		UserPrincipal userDetails = new UserPrincipal(user);

		assertTrue(jwtService.validateToken(token, userDetails), "Token should be valid for the correct user");
	}

	@Test
	void testValidateTokenWithWrongUser() {
		String username = "testuser";
		Role role = Role.MEMBER;

		String token = jwtService.generateAccessToken(username, role);

		User user = new User() {
			@Override
			public String getDisplayInfo() { return null; }
		};
		user.setUsername("wronguser");

		UserPrincipal userDetails = new UserPrincipal(user);

		assertFalse(jwtService.validateToken(token, userDetails), "Token should be invalid for the wrong user");
	}
}
