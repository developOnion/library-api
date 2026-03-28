package com.oop.library_management.user;

import com.oop.library_management.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@EntityListeners(AuditingEntityListener.class)
public abstract class User extends BaseEntity {

	@Column(nullable = false, unique = true, length = 30)
	@NotBlank(message = "Username is required")
	@Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
	private String username;
	@Column(nullable = false, length = 128)
	@NotBlank(message = "Password is required")
	@Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
	private String password;
	@Column(name = "first_name", nullable = false, length = 50)
	@NotBlank(message = "First name is required")
	@Size(max = 50, message = "First name must be at most 50 characters")
	private String firstName;
	@Column(name = "last_name", nullable = false, length = 50)
	@NotBlank(message = "Last name is required")
	@Size(max = 50, message = "Last name must be at most 50 characters")
	private String lastName;
	@NotNull(message = "Role is required")
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private Role role;
	@Column(name = "last_login")
	private LocalDateTime lastLogin;

	public User() {
	}

	public User(
		String username,
		String password,
		String firstName,
		String lastName,
		Role role
	) {

		this.username = username;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.role = role;
	}

	public abstract String getDisplayInfo();

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public LocalDateTime getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(LocalDateTime lastLogin) {
		this.lastLogin = lastLogin;
	}

	public String getFullName() {
		return firstName + " " + lastName;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}
}
