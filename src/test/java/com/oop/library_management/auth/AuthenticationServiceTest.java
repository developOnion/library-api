package com.oop.library_management.auth;

import com.oop.library_management.config.JwtService;
import com.oop.library_management.exception.AuthenticationException;
import com.oop.library_management.token.Token;
import com.oop.library_management.token.TokenRepository;
import com.oop.library_management.token.TokenResponseDTO;
import com.oop.library_management.user.Role;
import com.oop.library_management.user.User;
import com.oop.library_management.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthenticationServiceTest {

	@Mock
	private UserRepository userRepository;
	@Mock
	private AuthenticationManager authManager;
	@Mock
	private JwtService jwtService;
	@Mock
	private TokenRepository tokenRepository;
	@Mock
	private UserDetailsService userDetailsService;

	@InjectMocks
	private AuthService authService;

	@Test
	void authenticate_Success() {
		User testUser = mock(User.class);
		AuthRequestDTO request = new AuthRequestDTO("testuser", "password123");

		when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
		when(testUser.getId()).thenReturn(1L);
		when(testUser.getUsername()).thenReturn("testuser");
		when(testUser.getRole()).thenReturn(Role.MEMBER);
		when(jwtService.generateAccessToken("testuser", Role.MEMBER)).thenReturn("access-token");
		when(jwtService.generateRefreshToken("testuser")).thenReturn("refresh-token");
		when(tokenRepository.findAllValidTokenByUser(1L)).thenReturn(List.of());

		TokenResponseDTO response = authService.authenticate(request);

		assertNotNull(response);
		assertEquals("access-token", response.accessToken());
		assertEquals("refresh-token", response.refreshToken());

		verify(userRepository).save(testUser);
		verify(tokenRepository, times(2)).save(any(Token.class));
	}

	@Test
	void authenticate_Failure_InvalidCredentials() {
		AuthRequestDTO request = new AuthRequestDTO("testuser", "wrongpassword");

		doThrow(new org.springframework.security.core.AuthenticationException("Invalid credentials") {
		})
			.when(authManager).authenticate(any());

		assertThrows(AuthenticationException.class, () -> authService.authenticate(request));
	}

	@Test
	void refreshToken_Success() {
		User testUser = mock(User.class);
		String refreshToken = "valid-refresh-token";

		when(testUser.getId()).thenReturn(1L);
		when(testUser.getUsername()).thenReturn("testuser");
		when(testUser.getRole()).thenReturn(Role.MEMBER);
		when(jwtService.extractUsername(refreshToken)).thenReturn("testuser");
		when(userRepository.findByUsernameWithLock("testuser")).thenReturn(Optional.of(testUser));
		UserDetails testUserDetails = mock(UserDetails.class);
		when(userDetailsService.loadUserByUsername("testuser")).thenReturn(testUserDetails);
		when(jwtService.validateToken(refreshToken, testUserDetails)).thenReturn(true);
		Token mockToken = mock(Token.class);
		when(mockToken.isExpired()).thenReturn(false);
		when(mockToken.isRevoked()).thenReturn(false);
		when(tokenRepository.findByToken(refreshToken)).thenReturn(Optional.of(mockToken));
		when(jwtService.generateAccessToken("testuser", Role.MEMBER)).thenReturn("new-access-token");
		when(jwtService.generateRefreshToken("testuser")).thenReturn("new-refresh-token");
		when(tokenRepository.findAllValidTokenByUser(1L)).thenReturn(List.of());

		TokenResponseDTO response = authService.refreshToken(refreshToken);

		assertNotNull(response);
		assertEquals("new-access-token", response.accessToken());
		assertEquals(refreshToken, response.refreshToken());

		verify(tokenRepository, times(1)).save(any(Token.class));
	}

	@Test
	void refreshToken_Failure_InvalidToken() {
		User testUser = mock(User.class);
		String refreshToken = "invalid-refresh-token";

		when(jwtService.extractUsername(refreshToken)).thenReturn("testuser");
		when(userRepository.findByUsernameWithLock("testuser")).thenReturn(Optional.of(testUser));
		UserDetails testUserDetails = mock(UserDetails.class);
		when(userDetailsService.loadUserByUsername("testuser")).thenReturn(testUserDetails);
		when(jwtService.validateToken(refreshToken, testUserDetails)).thenReturn(false);

		assertThrows(AuthenticationException.class, () -> authService.refreshToken(refreshToken));
	}

	@Test
	void revokeAllTokens_Success() {
		User testUser = mock(User.class);
		String username = "testuser";
		Token token = mock(Token.class);

		when(userRepository.findByUsernameWithLock(username)).thenReturn(Optional.of(testUser));
		when(testUser.getId()).thenReturn(1L);
		when(tokenRepository.findAllValidTokenByUser(1L)).thenReturn(List.of(token));

		authService.revokeAllTokens(username);

		verify(token).setExpired(true);
		verify(token).setRevoked(true);
		verify(tokenRepository).saveAll(List.of(token));
	}
}