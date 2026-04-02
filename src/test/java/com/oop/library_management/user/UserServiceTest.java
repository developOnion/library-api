package com.oop.library_management.user;

import com.oop.library_management.exception.InvalidUserDataException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserRepository userRepository;
	@Mock
	private UserMapper userMapper;
	@Mock
	private PasswordEncoder passwordEncoder;
	@Mock
	private UserFactory userFactory;

	@InjectMocks
	private UserService userService;

	@Test
	void registerMember_Success() {
		UserRequestDTO request = new UserRequestDTO(
			"user1",
			"pass123",
			"First",
			"Last",
			null
		);

		when(userRepository.existsByUsername("user1")).thenReturn(false);
		when(passwordEncoder.encode("pass123")).thenReturn("encodedPass");

		Member member = mock(Member.class);
		when(userFactory.createUser(eq(request), eq(Role.MEMBER), eq("encodedPass")))
			.thenReturn(member);
		when(userRepository.save(any(User.class))).thenReturn(member);

		userService.registerMember(request);

		verify(userFactory).createUser(eq(request), eq(Role.MEMBER), eq("encodedPass"));
		verify(userRepository).save(member);
		verify(userMapper).toDTO(member);
	}

	@Test
	void registerLibrarian_Success() {
		UserRequestDTO request = new UserRequestDTO(
			"lib1",
			"pass123",
			"First",
			"Last",
			LibrarianPosition.HEAD_LIBRARIAN
		);

		when(userRepository.existsByUsername("lib1")).thenReturn(false);
		when(passwordEncoder.encode("pass123")).thenReturn("encodedPass");

		Librarian librarian = mock(Librarian.class);
		when(userFactory.createUser(eq(request), eq(Role.LIBRARIAN), eq("encodedPass")))
			.thenReturn(librarian);
		when(userRepository.save(any(User.class))).thenReturn(librarian);

		userService.registerLibrarian(request);

		verify(userFactory).createUser(eq(request), eq(Role.LIBRARIAN), eq("encodedPass"));
		verify(userRepository).save(librarian);
		verify(userMapper).toDTO(librarian);
	}

	@Test
	void registerMember_Failure_DuplicateUsername() {
		UserRequestDTO request = new UserRequestDTO("user1", "pass123", "F", "L", null);
		when(userRepository.existsByUsername("user1")).thenReturn(true);

		assertThrows(InvalidUserDataException.class, () -> userService.registerMember(request));

		verify(userFactory, never()).createUser(any(), any(), anyString());
		verify(userRepository, never()).save(any());
	}
}
