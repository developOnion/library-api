package com.oop.library_management.user;

import com.oop.library_management.exception.InvalidUserDataException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserFactoryTest {

	private UserFactory userFactory;

	@BeforeEach
	void setUp() {
		userFactory = new UserFactoryImpl();
	}

	@Test
	void createUser_Member_Success() {
		UserRequestDTO request = new UserRequestDTO(
			"testuser",
			"password123",
			"First",
			"Last",
			null
		);

		User user = userFactory.createUser(request, Role.MEMBER, "encodedPassword");

		assertTrue(user instanceof Member);
		assertEquals("testuser", user.getUsername());
		assertEquals("encodedPassword", user.getPassword());
		assertEquals(Role.MEMBER, user.getRole());
	}

	@Test
	void createUser_Librarian_Success() {
		UserRequestDTO request = new UserRequestDTO(
			"libuser",
			"password123",
			"First",
			"Last",
			LibrarianPosition.HEAD_LIBRARIAN
		);

		User user = userFactory.createUser(request, Role.LIBRARIAN, "encodedPassword");

		assertTrue(user instanceof Librarian);
		assertEquals("libuser", user.getUsername());
		assertEquals(LibrarianPosition.HEAD_LIBRARIAN, ((Librarian) user).getPosition());
		assertEquals(Role.LIBRARIAN, user.getRole());
	}

	@Test
	void createUser_Librarian_Failure_MissingPosition() {
		UserRequestDTO request = new UserRequestDTO(
			"libuser",
			"password123",
			"First",
			"Last",
			null
		);

		assertThrows(InvalidUserDataException.class, () -> 
			userFactory.createUser(request, Role.LIBRARIAN, "encodedPassword")
		);
	}
}
