package com.oop.library_management.book;

import com.oop.library_management.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books")
@Tag(name = "Book Management", description = "Endpoints for managing books in the library")
public class BookController {

	private final BookService bookService;

	public BookController(BookService bookService) {
		this.bookService = bookService;
	}

	@GetMapping("/{id}")
	public ResponseEntity<BookResponseDTO> getBookById(
		@PathVariable @Min(1) Long id
	) {

		BookResponseDTO book = bookService.getById(id);
		return ResponseEntity.ok().body(book);
	}

	@GetMapping
	public ResponseEntity<PageResponse<BookResponseDTO>> findAllBooks(
		@RequestParam(name = "page", defaultValue = "0", required = false) @Min(0) int page,
		@RequestParam(name = "size", defaultValue = "10", required = false) @Min(1) @Max(100) int size
	) {

		PageResponse<BookResponseDTO> books = bookService.findAllBooks(page, size);

		return ResponseEntity.ok().body(books);
	}

	@PreAuthorize("hasAuthority('LIBRARIAN')")
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<BookResponseDTO> createBook(
		@Valid @RequestBody BookRequestDTO bookRequestDTO
	) {

		BookResponseDTO createdBook = bookService.create(bookRequestDTO);

		return ResponseEntity.ok().body(createdBook);
	}

	@PreAuthorize("hasAuthority('LIBRARIAN')")
	@DeleteMapping("{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<Void> deleteBook(
		@PathVariable @Min(1) Long id
	) {

		bookService.delete(id);

		return ResponseEntity.noContent().build();
	}

	@PreAuthorize("hasAuthority('LIBRARIAN')")
	@PutMapping("{id}")
	public ResponseEntity<BookResponseDTO> updateBook(
		@PathVariable @Min(1) Long id,
		@Valid @RequestBody BookRequestDTO bookRequestDTO
	) {

		BookResponseDTO updatedBook = bookService.update(id, bookRequestDTO);

		return ResponseEntity.ok().body(updatedBook);
	}
}
