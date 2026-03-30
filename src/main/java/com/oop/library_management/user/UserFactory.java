package com.oop.library_management.user;

public interface UserFactory {
	User createUser(UserRequestDTO request, Role role, String encodedPassword);
}
