package com.oop.library_management.controller;

import com.oop.library_management.dto.author.AuthorRequestDTO;
import com.oop.library_management.dto.author.AuthorResponseDTO;
import com.oop.library_management.dto.search_criteria.AuthorSearchCriteria;
import com.oop.library_management.model.common.PageResponse;
import com.oop.library_management.service.AuthorService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authors")
@Tag(name = "Author Management", description = "Endpoints for managing authors in the library")
public class AuthorController {

	private final AuthorService authorService;

	public AuthorController(
		AuthorService authorService
	) {
		this.authorService = authorService;
	}

	@GetMapping
	public ResponseEntity<PageResponse<AuthorResponseDTO>> searchAuthorsByName(
		@RequestParam(name = "page", defaultValue = "0", required = false) @Min(0) int page,
		@RequestParam(name = "size", defaultValue = "10", required = false) @Min(1) @Max(100) int size,
		@ModelAttribute AuthorSearchCriteria criteria
	) {

		PageResponse<AuthorResponseDTO> authors = authorService.search(criteria, page, size);

		return ResponseEntity.ok().body(authors);
	}

	@PreAuthorize("hasAuthority('LIBRARIAN')")
	@GetMapping("/{id}")
	public ResponseEntity<AuthorResponseDTO> getAuthorById(
		@PathVariable @Min(1) Long id
	) {

		AuthorResponseDTO author = authorService.getById(id);

		return ResponseEntity.ok().body(author);
	}

	@PreAuthorize("hasAuthority('LIBRARIAN')")
	@PostMapping
	public ResponseEntity<AuthorResponseDTO> createAuthor(
		@Parameter(description = "Author data to create", required = true)
		@Valid @RequestBody AuthorRequestDTO authorRequestDTO
	) {

		AuthorResponseDTO createdAuthor = authorService.create(authorRequestDTO);

		return ResponseEntity.ok().body(createdAuthor);
	}
}
