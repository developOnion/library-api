package com.oop.library_management.security;

import com.oop.library_management.auth.TokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class JwtFilter extends OncePerRequestFilter {

	private final JwtService jwtUtil;
	private final UserDetailsServiceImpl userDetailsService;
	private final TokenRepository tokenRepository;

	public JwtFilter(
			JwtService jwtUtil,
			UserDetailsServiceImpl userDetailsService,
			TokenRepository tokenRepository
	) {

		this.jwtUtil = jwtUtil;
		this.userDetailsService = userDetailsService;
		this.tokenRepository = tokenRepository;
	}

	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			@NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain
	) throws ServletException, IOException {

		if (request.getServletPath().contains("/api/v1/auth")) {
			filterChain.doFilter(request, response);
			return;
		}

		final String authHeader = request.getHeader("Authorization");
		final String token;
		final String username;

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		try {

			token = authHeader.substring(7);
			username = jwtUtil.extractUsername(token);

			if (username != null &&
					SecurityContextHolder.getContext().getAuthentication() == null
			) {

				UserDetails userDetails = userDetailsService.loadUserByUsername(username);

				boolean isTokenValid = tokenRepository.findByToken(token)
					.map(t -> !t.isExpired() && !t.isRevoked())
					.orElse(false);

				if (jwtUtil.validateToken(token, userDetails)) {
					if (isTokenValid) {
						UsernamePasswordAuthenticationToken authToken =
								new UsernamePasswordAuthenticationToken(
										userDetails,
										null,
										userDetails.getAuthorities()
								);
						authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

						SecurityContextHolder.getContext().setAuthentication(authToken);
					} else {
						handleJwtException(response, "Token has been revoked or expired");
						return;
					}
				}
			}

			filterChain.doFilter(request, response);
		} catch (SignatureException e) {
			handleJwtException(response, "Invalid JWT signature");
		} catch (ExpiredJwtException e) {
			handleJwtException(response, "JWT token has expired");
		} catch (MalformedJwtException e) {
			handleJwtException(response, "Invalid JWT token");
		}
	}

	private void handleJwtException(
			HttpServletResponse response,
			String message
	) throws IOException {

		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json");
		response.getWriter().write(
				"{\"timestamp\":\"" + LocalDateTime.now() + "\"," +
						"\"status\":401," +
						"\"message\":\"" + message + "\"}"
		);
	}
}
