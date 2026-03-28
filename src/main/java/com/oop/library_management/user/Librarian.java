package com.oop.library_management.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "librarians")
public class Librarian extends User {

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@NotNull(message = "Librarian position is required")
	private LibrarianPosition position;

	protected Librarian() {
		// for JPA
	}

	public Librarian(
		String username,
		String password,
		String firstName,
		String lastName,
		Role role,
		LibrarianPosition position
	) {

		super(username, password, firstName, lastName, role);
		this.position = position;
	}

	public LibrarianPosition getPosition() {
		return position;
	}

	public void setPosition(LibrarianPosition position) {
		this.position = position;
	}

	@Override
	public String getDisplayInfo() {
		return "Librarian [" + position + "] " + getFullName();
	}
}
