package com.oop.library_management.security;

import com.oop.library_management.user.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
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

	@Value("${application.security.jwt.expiration}")
	private Long EXPIRATION_TIME;

	public String generateToken(String username, Role role) {

		Map<String, Object> claims = new HashMap<>();
		claims.put("role", role.name());

		return Jwts.builder()
			.claims()
			.add(claims)
			.subject(username)
			.issuedAt(new Date(System.currentTimeMillis()))
			.expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
			.and()
			.signWith(generateKey())
			.compact();
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
