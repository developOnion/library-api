package com.oop.library_management.user;

import com.oop.library_management.auth.Token;
import com.oop.library_management.auth.TokenRepository;
import com.oop.library_management.auth.TokenType;
import com.oop.library_management.auth.AuthRequestDTO;
import com.oop.library_management.exception.AuthenticationException;
import com.oop.library_management.exception.InvalidUserDataException;
import com.oop.library_management.user.UserMapper;
import com.oop.library_management.security.JwtService;
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

	public UserService(
		UserRepository userRepository,
		UserMapper userMapper,
		AuthenticationManager authManager,
		JwtService jwtService,
		PasswordEncoder passwordEncoder,
		TokenRepository tokenRepository
	) {

		this.userMapper = userMapper;
		this.userRepository = userRepository;
		this.authManager = authManager;
		this.jwtService = jwtService;
		this.passwordEncoder = passwordEncoder;
		this.tokenRepository = tokenRepository;
	}

	@Transactional
	public UserResponseDTO registerMember(UserRequestDTO request) {

		validateUserRequest(request);

		User user = new Member(
			request.username(),
			passwordEncoder.encode(request.password()),
			request.firstName(),
			request.lastName(),
			Role.MEMBER
		);

		User savedUser = userRepository.save(user);

		return userMapper.toDTO(savedUser);
	}

	@Transactional
	public UserResponseDTO registerLibrarian(UserRequestDTO userDTO) {

		validateLibrarianRequest(userDTO);

		User user = new Librarian(
			userDTO.username(),
			passwordEncoder.encode(userDTO.password()),
			userDTO.firstName(),
			userDTO.lastName(),
			Role.LIBRARIAN,
			userDTO.position()
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
				new com.oop.library_management.security.UserPrincipal(user);

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

	public record TokenResponse(String accessToken, String refreshToken) {}

	private boolean isValidPassword(String password) {

		boolean hasNumber = password.matches(".*\\d.*");
		boolean hasCharacter = password.matches(".*[a-zA-Z].*");

		return hasNumber && hasCharacter;
	}

	private boolean isValidUsername(String username) {

		boolean hasNumber = username.matches(".*\\d.*");
		boolean hasOnlyLettersAndNumbers = username.matches("^[a-zA-Z0-9]+$");

		return hasOnlyLettersAndNumbers && hasNumber;
	}

	private void validateLibrarianRequest(UserRequestDTO userDTO) {

		validateUserRequest(userDTO);

		if (userDTO.position() == null) {
			throw new InvalidUserDataException("Librarian position is required");
		}
	}

	private void validateUserRequest(UserRequestDTO userDTO) {

		if (userRepository.existsByUsername(userDTO.username())) {
			throw new InvalidUserDataException("Username already exists");
		}

		if (!isValidUsername(userDTO.username())) {
			throw new InvalidUserDataException(
				"Username must be 3-30 characters long and contain only letters and numbers");
		}

		if (!isValidPassword(userDTO.password())) {
			throw new InvalidUserDataException(
				"Password must contain at least one number and one character");
		}
	}
}
