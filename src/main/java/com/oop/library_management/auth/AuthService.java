package com.oop.library_management.auth;

import com.oop.library_management.config.JwtService;
import com.oop.library_management.exception.AuthenticationException;
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
			User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new AuthenticationException("User not found"));

			UserDetails userDetails = userDetailsService.loadUserByUsername(username);

			if (jwtService.validateToken(refreshToken, userDetails)) {

				String accessToken = jwtService.generateAccessToken(
					user.getUsername(),
					user.getRole()
				);

				String newRefreshToken = jwtService.generateRefreshToken(
					user.getUsername()
				);

				revokeAllUserTokens(user);
				saveUserToken(user, accessToken);
				saveUserToken(user, newRefreshToken);

				return new TokenResponseDTO(accessToken, newRefreshToken);
			}
		}

		throw new AuthenticationException("Invalid refresh token");
	}

	@Transactional
	public void revokeAllTokens(String username) {
		User user = userRepository.findByUsername(username)
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