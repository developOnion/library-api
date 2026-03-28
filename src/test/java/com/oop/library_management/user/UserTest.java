package com.oop.library_management.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

	@Test
	void validate_Success() {
		User user = new Member("user123", "pass123", "First", "Last", Role.MEMBER);
		assertDoesNotThrow(user::validate);
	}

	@Test
	void validate_Success_UsernameNoNumber() {
		User user = new Member("username", "pass123", "First", "Last", Role.MEMBER);
		assertDoesNotThrow(user::validate);
	}

	@Test
	void validate_Failure_UsernameSpecialChars() {
		User user = new Member("user!123", "pass123", "First", "Last", Role.MEMBER);
		assertThrows(IllegalArgumentException.class, user::validate);
	}

	@Test
	void validate_Failure_PasswordNoNumber() {
		User user = new Member("user123", "password", "First", "Last", Role.MEMBER);
		assertThrows(IllegalArgumentException.class, user::validate);
	}

	@Test
	void validate_Failure_PasswordNoCharacter() {
		User user = new Member("user123", "12345678", "First", "Last", Role.MEMBER);
		assertThrows(IllegalArgumentException.class, user::validate);
	}

	@Test
	void validate_EncodedPassword_SkipsValidation() {
		// BCrypt hash
		String encoded = "$2a$10$8.UnVuG9shgButw1be5wOtJyJZmipTHUtSUXhwSnd6S1BIp4p.v6m";
		User user = new Member("user123", encoded, "First", "Last", Role.MEMBER);
		assertDoesNotThrow(user::validate);
	}
}
