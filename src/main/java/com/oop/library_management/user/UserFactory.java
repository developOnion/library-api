package com.oop.library_management.user;

public interface UserFactory {
	/**
	 * Creates a User object based on the provided request and role.
	 *
	 * @param request         The user creation request data.
	 * @param role            The role of the user to be created.
	 * @param encodedPassword The already encoded password.
	 * @return A concrete User object (Member or Librarian).
	 */
	User createUser(UserRequestDTO request, Role role, String encodedPassword);
}
