package com.oop.library_management.security;

import com.oop.library_management.user.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

	@Value("${application.security.jwt.secret-key}")
	private String SECRET_KEY;

	@Value("${application.security.jwt.access-token-expiration}")
	private Long ACCESS_TOKEN_EXPIRATION;

	@Value("${application.security.jwt.refresh-token-expiration}")
	private Long REFRESH_TOKEN_EXPIRATION;

	public String generateAccessToken(String username, Role role) {

		Map<String, Object> claims = new HashMap<>();
		claims.put("role", role.name());

		return buildToken(claims, username, ACCESS_TOKEN_EXPIRATION);
	}

	public String generateRefreshToken(String username) {
		return buildToken(new HashMap<>(), username, REFRESH_TOKEN_EXPIRATION);
	}

	private String buildToken(
		Map<String, Object> claims,
		String username,
		long expiration
	) {
		return Jwts.builder()
			.claims()
			.add(claims)
			.subject(username)
			.issuedAt(new Date(System.currentTimeMillis()))
			.expiration(new Date(System.currentTimeMillis() + expiration))
			.and()
			.signWith(generateKey())
			.compact();
	}

	public ResponseCookie createRefreshTokenCookie(String refreshToken) {
		return ResponseCookie.from("refresh_token", refreshToken)
			.httpOnly(true)
			.secure(false)
			.path("/api/v1/auth/refresh-token")
			.maxAge(REFRESH_TOKEN_EXPIRATION / 1000)
			.sameSite("Strict")
			.build();
	}

	public Key generateKey() {

		byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);

		return Keys.hmacShaKeyFor(keyBytes);
	}

	public boolean validateToken(String token, UserDetails userDetails) {

		final String username = extractUsername(token);
		boolean isUsernameValid = username.equals(userDetails.getUsername());
		boolean isTokenExpired = isTokenExpired(token);

		return isUsernameValid && !isTokenExpired;
	}

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	private boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	private Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {

		final Claims claims = extractAllClaims(token);

		return claimsResolver.apply(claims);

	}

	private Claims extractAllClaims(String token) {

		return Jwts.parser()
			.verifyWith((SecretKey) generateKey())
			.build()
			.parseSignedClaims(token)
			.getPayload();
	}

}
