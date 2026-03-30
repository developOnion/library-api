package com.oop.library_management.user;

import com.oop.library_management.auth.*;
import com.oop.library_management.config.JwtService;
import com.oop.library_management.exception.AuthenticationException;
import com.oop.library_management.exception.InvalidUserDataException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final AuthenticationManager authManager;
	private final JwtService jwtService;
	private final PasswordEncoder passwordEncoder;
	private final TokenRepository tokenRepository;
	private final UserFactory userFactory;

	public UserService(
		UserRepository userRepository,
		UserMapper userMapper,
		AuthenticationManager authManager,
		JwtService jwtService,
		PasswordEncoder passwordEncoder,
		TokenRepository tokenRepository,
		UserFactory userFactory
	) {

		this.userMapper = userMapper;
		this.userRepository = userRepository;
		this.authManager = authManager;
		this.jwtService = jwtService;
		this.passwordEncoder = passwordEncoder;
		this.tokenRepository = tokenRepository;
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

	@Transactional
	public TokenResponse authenticate(AuthRequestDTO loginRequest) {

		try {
			// Authenticate user credentials
			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
				loginRequest.username(),
				loginRequest.password()
			);

			authManager.authenticate(authToken);

			// Fetch user from database to get the role
			User user = userRepository.findByUsername(loginRequest.username())
				.orElseThrow(() -> new AuthenticationException("User not found"));

			// Update last login
			user.setLastLogin(java.time.LocalDateTime.now());
			userRepository.save(user);

			String accessToken = jwtService.generateAccessToken(
				user.getUsername(),
				user.getRole()
			);

			String refreshToken = jwtService.generateRefreshToken(
				user.getUsername()
			);

			revokeAllUserTokens(user);
			saveUserToken(user, accessToken);
			saveUserToken(user, refreshToken);

			return new TokenResponse(accessToken, refreshToken);
		} catch (org.springframework.security.core.AuthenticationException e) {
			throw new AuthenticationException("Invalid username or password");
		}
	}

	private void revokeAllUserTokens(User user) {
		var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
		if (validUserTokens.isEmpty())
			return;
		validUserTokens.forEach(token -> {
			token.setExpired(true);
			token.setRevoked(true);
		});
		tokenRepository.saveAll(validUserTokens);
	}

	private void saveUserToken(User user, String jwtToken) {
		var token = new Token(jwtToken, TokenType.BEARER, false, false, user);
		tokenRepository.save(token);
	}

	@Transactional
	public void revokeAllTokens(String username) {
		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new AuthenticationException("User not found"));
		revokeAllUserTokens(user);
	}

	@Transactional
	public TokenResponse refreshToken(String refreshToken) {

		String username = jwtService.extractUsername(refreshToken);

		if (username != null) {

			User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new AuthenticationException("User not found"));

			// Load user details for validation
			org.springframework.security.core.userdetails.UserDetails userDetails =
				new UserPrincipal(user);

			if (jwtService.validateToken(refreshToken, userDetails)) {

				String accessToken = jwtService.generateAccessToken(
					user.getUsername(),
					user.getRole()
				);

				// Rotation: generate a new refresh token as well
				String newRefreshToken = jwtService.generateRefreshToken(
					user.getUsername()
				);

				revokeAllUserTokens(user);
				saveUserToken(user, accessToken);
				saveUserToken(user, newRefreshToken);

				return new TokenResponse(accessToken, newRefreshToken);
			}
		}

		throw new AuthenticationException("Invalid refresh token");
	}

	private void validateUserRequest(UserRequestDTO userDTO) {

		if (userRepository.existsByUsername(userDTO.username())) {
			throw new InvalidUserDataException("Username already exists");
		}
	}

	public record TokenResponse(String accessToken, String refreshToken) {
	}
}
