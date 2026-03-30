package com.oop.library_management.config;

import com.oop.library_management.auth.TokenRepository;
import com.oop.library_management.auth.UserDetailsServiceImpl;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

	private final JwtService jwtUtil;
	private final UserDetailsServiceImpl userDetailsService;
	private final TokenRepository tokenRepository;
	private final JwtService jwtService;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

	public JwtFilter(
		JwtService jwtUtil,
		UserDetailsServiceImpl userDetailsService,
		TokenRepository tokenRepository,
		JwtService jwtService, JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint
	) {

		this.jwtUtil = jwtUtil;
		this.userDetailsService = userDetailsService;
		this.tokenRepository = tokenRepository;
		this.jwtService = jwtService;
		this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
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
						jwtAuthenticationEntryPoint.commence(request, response, new AuthenticationException("Token is invalid or has been revoked") {
						});
						return;
					}
				}
			}

			filterChain.doFilter(request, response);
		} catch (JwtException e) {
			SecurityContextHolder.clearContext();
			String clientMessage = jwtService.mapExceptionToClientMessage(e);
			jwtAuthenticationEntryPoint.commence(request, response, new AuthenticationException(clientMessage) {
			});
		}
	}
}
