package com.oop.library_management.auth;

import com.oop.library_management.config.JwtService;
import com.oop.library_management.exception.AuthenticationException;
import com.oop.library_management.token.Token;
import com.oop.library_management.token.TokenRepository;
import com.oop.library_management.token.TokenResponseDTO;
import com.oop.library_management.token.TokenType;
import com.oop.library_management.user.User;
import com.oop.library_management.user.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {

	private static final int REFRESH_GRACE_PERIOD_SECONDS = 10;
	private final UserRepository userRepository;
	private final AuthenticationManager authManager;
	private final JwtService jwtService;
	private final TokenRepository tokenRepository;
	private final UserDetailsService userDetailsService;

	public AuthService(
		UserRepository userRepository,
		AuthenticationManager authManager,
		JwtService jwtService,
		TokenRepository tokenRepository,
		UserDetailsService userDetailsService
	) {
		this.userRepository = userRepository;
		this.authManager = authManager;
		this.jwtService = jwtService;
		this.tokenRepository = tokenRepository;
		this.userDetailsService = userDetailsService;
	}

	@Transactional
	public TokenResponseDTO authenticate(AuthRequestDTO loginRequest) {

		try {
			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
				loginRequest.username(),
				loginRequest.password()
			);

			authManager.authenticate(authToken);

			User user = userRepository.findByUsername(loginRequest.username())
				.orElseThrow(() -> new AuthenticationException("User not found"));

			user.setLastLogin(LocalDateTime.now());
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

			return new TokenResponseDTO(accessToken, refreshToken);
		} catch (org.springframework.security.core.AuthenticationException e) {
			throw new AuthenticationException("Invalid username or password");
		}
	}

	@Transactional
	public TokenResponseDTO refreshToken(String refreshToken) {

		String username = jwtService.extractUsername(refreshToken);

		if (username != null) {
			// Serialize refresh requests for the same user to prevent race conditions
			User user = userRepository.findByUsernameWithLock(username)
				.orElseThrow(() -> new AuthenticationException("User not found"));

			UserDetails userDetails = userDetailsService.loadUserByUsername(username);

			// First, check if there's a recently generated token (concurrency grace period)
			final var validTokens = tokenRepository.findAllValidTokenByUser(user.getId());
			final var recentToken = validTokens.stream()
				.filter(t -> t.getCreatedAt() != null &&
					t.getCreatedAt().isAfter(LocalDateTime.now().minusSeconds(REFRESH_GRACE_PERIOD_SECONDS)))
				.findFirst();

			if (recentToken.isPresent()) {
				// If we found a recently created token, return it instead of generating a new one
				return new TokenResponseDTO(recentToken.get().getToken(), refreshToken);
			}

			// If no recent token, validate the incoming refresh token against the DB
			boolean isRefreshTokenValid = tokenRepository.findByToken(refreshToken)
				.map(t -> !t.isExpired() && !t.isRevoked())
				.orElse(false);

			if (isRefreshTokenValid && jwtService.validateToken(refreshToken, userDetails)) {
				String accessToken = jwtService.generateAccessToken(
					user.getUsername(),
					user.getRole()
				);
				revokeAllUserTokens(user);
				saveUserToken(user, accessToken);

				return new TokenResponseDTO(accessToken, refreshToken);
			}
		}

		throw new AuthenticationException("Invalid or revoked refresh token");
	}

	@Transactional
	public void revokeAllTokens(String username) {
		User user = userRepository.findByUsernameWithLock(username)
			.orElseThrow(() -> new AuthenticationException("User not found"));
		revokeAllUserTokens(user);
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
}