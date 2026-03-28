package com.oop.library_management.book;

import com.oop.library_management.author.AuthorMapper;
import com.oop.library_management.category.CategoryMapper;
import com.oop.library_management.common.BaseMapper;
import org.springframework.stereotype.Component;

@Component
public class BookMapper extends BaseMapper<Book, BookResponseDTO> {

	private final AuthorMapper authorMapper;
	private final CategoryMapper categoryMapper;

	public BookMapper(
		AuthorMapper authorMapper,
		CategoryMapper categoryMapper
	) {

		this.authorMapper = authorMapper;
		this.categoryMapper = categoryMapper;
	}

	@Override
	public BookResponseDTO toDTO(Book book) {

		if (book == null) {
			return null;
		}

		return new BookResponseDTO(
			book.getId(),
			book.getTitle(),
			book.getIsbn(),
			book.getTotalCopies(),
			book.getAvailableCopies(),
			book.getAuthors().stream()
				.map(authorMapper::toDTO)
				.toList(),
			book.getCategories().stream()
				.map(categoryMapper::toDTO)
				.toList()
		);
	}
}
