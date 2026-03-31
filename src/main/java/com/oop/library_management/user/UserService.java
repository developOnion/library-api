package com.oop.library_management.user;

import com.oop.library_management.exception.InvalidUserDataException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final PasswordEncoder passwordEncoder;
	private final UserFactory userFactory;

	public UserService(
		UserRepository userRepository,
		UserMapper userMapper,
		PasswordEncoder passwordEncoder,
		UserFactory userFactory
	) {
		this.userRepository = userRepository;
		this.userMapper = userMapper;
		this.passwordEncoder = passwordEncoder;
		this.userFactory = userFactory;
	}

	@Transactional
	public UserResponseDTO registerMember(UserRequestDTO request) {
		validateUserRequest(request);

		User user = userFactory.createUser(
			request,
			Role.MEMBER,
			passwordEncoder.encode(request.password())
		);

		User savedUser = userRepository.save(user);

		return userMapper.toDTO(savedUser);
	}

	@Transactional
	public UserResponseDTO registerLibrarian(UserRequestDTO request) {
		validateUserRequest(request);

		User user = userFactory.createUser(
			request,
			Role.LIBRARIAN,
			passwordEncoder.encode(request.password())
		);

		User savedUser = userRepository.save(user);

		return userMapper.toDTO(savedUser);
	}

	private void validateUserRequest(UserRequestDTO userDTO) {
		if (userRepository.existsByUsername(userDTO.username())) {
			throw new InvalidUserDataException("Username already exists");
		}
	}
}