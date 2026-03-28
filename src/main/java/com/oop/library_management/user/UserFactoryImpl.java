package com.oop.library_management.user;

import com.oop.library_management.exception.InvalidUserDataException;
import org.springframework.stereotype.Component;

@Component
public class UserFactoryImpl implements UserFactory {

	@Override
	public User createUser(
		UserRequestDTO request,
		Role role,
		String encodedPassword
	) {

		return switch (role) {
			case MEMBER -> new Member(
				request.username(),
				encodedPassword,
				request.firstName(),
				request.lastName(),
				Role.MEMBER
			);
			case LIBRARIAN -> {
				if (request.position() == null) {
					throw new InvalidUserDataException("Librarian position is required");
				}
				yield new Librarian(
					request.username(),
					encodedPassword,
					request.firstName(),
					request.lastName(),
					Role.LIBRARIAN,
					request.position()
				);
			}
			default -> throw new InvalidUserDataException("Unsupported user role: " + role);
		};
	}
}
