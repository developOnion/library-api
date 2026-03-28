package com.oop.library_management.book;

import com.oop.library_management.author.AuthorResponseDTO;
import com.oop.library_management.category.CategoryResponseDTO;

import java.util.List;

public record BookResponseDTO(

	Long id,
	String title,
	String isbn,
	int totalCopies,
	int availableCopies,
	List<AuthorResponseDTO> authors,
	List<CategoryResponseDTO> categories
) {
}
