package com.oop.library_management.token;

import com.oop.library_management.common.BaseEntity;
import com.oop.library_management.user.User;
import jakarta.persistence.*;

@Entity
@Table(name = "tokens")
public class Token extends BaseEntity {

	@Column(unique = true, nullable = false)
	private String token;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TokenType tokenType = TokenType.BEARER;

	@Column(nullable = false)
	private boolean revoked;

	@Column(nullable = false)
	private boolean expired;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	public Token() {
	}

	public Token(String token, TokenType tokenType, boolean revoked, boolean expired, User user) {
		this.token = token;
		this.tokenType = tokenType;
		this.revoked = revoked;
		this.expired = expired;
		this.user = user;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public boolean isRevoked() {
		return revoked;
	}

	public void setRevoked(boolean revoked) {
		this.revoked = revoked;
	}

	public boolean isExpired() {
		return expired;
	}

	public void setExpired(boolean expired) {
		this.expired = expired;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}