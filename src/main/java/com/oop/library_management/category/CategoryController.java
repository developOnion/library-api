package com.oop.library_management.category;

import com.oop.library_management.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
@Tag(name = "Category Management", description = "Endpoints for managing book categories in the library")
public class CategoryController {

	private final CategoryService categoryService;

	public CategoryController(
		CategoryService categoryService
	) {
		this.categoryService = categoryService;
	}

	@GetMapping
	public ResponseEntity<PageResponse<CategoryResponseDTO>> searchCategoriesByName(
		@RequestParam(name = "page", defaultValue = "0", required = false) @Min(0) int page,
		@RequestParam(name = "size", defaultValue = "10", required = false) @Min(1) @Max(100) int size,
		@ModelAttribute CategorySearchCriteria criteria
	) {

		PageResponse<CategoryResponseDTO> categories =
			categoryService.search(criteria, page, size);

		return ResponseEntity.ok().body(categories);
	}

	@PreAuthorize("hasAuthority('LIBRARIAN')")
	@GetMapping("/{id}")
	public ResponseEntity<CategoryResponseDTO> getCategoryById(
		@PathVariable @Min(1) Long id
	) {

		CategoryResponseDTO category =
			categoryService.getById(id);

		return ResponseEntity.ok().body(category);
	}

	@PreAuthorize("hasAuthority('LIBRARIAN')")
	@PostMapping
	public ResponseEntity<CategoryResponseDTO> createCategory(
		@Valid @RequestBody CategoryRequestDTO categoryRequestDTO
	) {

		CategoryResponseDTO createdCategory =
			categoryService.create(categoryRequestDTO);

		return ResponseEntity.ok().body(createdCategory);
	}
}
