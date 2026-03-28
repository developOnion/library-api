package com.oop.library_management.author;

import com.oop.library_management.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "authors")
public class Author extends BaseEntity {

	@Column(name = "full_name", nullable = false, length = 100)
	@NotBlank(message = "Full name is required")
	@Size(max = 100, message = "Full name must be at most 100 characters")
	private String fullName;

	@Column(name = "first_name", length = 50)
	@Size(max = 50, message = "First name must be at most 50 characters")
	private String firstName;

	@Column(name = "last_name", length = 50)
	@Size(max = 50, message = "Last name must be at most 50 characters")
	private String lastName;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@NotNull(message = "Author type is required")
	private AuthorType type;

	protected Author() {
	}

	public Author(
		String firstName,
		String lastName,
		AuthorType type
	) {

		this.firstName = firstName;
		this.lastName = lastName;
		updateFullName();
		this.type = type;
	}

	public AuthorType getType() {
		return type;
	}

	public void setType(AuthorType type) {
		this.type = type;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
		updateFullName();
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
		updateFullName();
	}

	public String getFullName() {
		return this.fullName;
	}

	@PrePersist
	@PreUpdate
	public void updateFullName() {

		if (this.firstName != null && this.lastName != null) {
			this.fullName = this.firstName + " " + this.lastName;
		} else if (this.firstName != null) {
			this.fullName = this.firstName;
		} else if (this.lastName != null) {
			this.fullName = this.lastName;
		} else {
			this.fullName = "";
		}
	}
}
