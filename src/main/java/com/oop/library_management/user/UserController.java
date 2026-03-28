package com.oop.library_management.user;

import com.oop.library_management.validation.validationgroup.CreateLibrarianValidation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.groups.Default;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/register")
@Tag(name = "User Management", description = "Endpoints for managing library users")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PreAuthorize("hasAuthority('LIBRARIAN')")
	@PostMapping("members")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<UserResponseDTO> registerMember(
		@Validated({Default.class})
		@RequestBody UserRequestDTO userDTO
	) {

		UserResponseDTO createdUser = userService.registerMember(userDTO);

		return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
	}

	@PreAuthorize("hasAuthority('LIBRARIAN')")
	@PostMapping("librarians")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<UserResponseDTO> registerLibrarian(
		@Validated({Default.class, CreateLibrarianValidation.class})
		@RequestBody UserRequestDTO userDTO
	) {

		UserResponseDTO createdUser = userService.registerLibrarian(userDTO);

		return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
	}
}
