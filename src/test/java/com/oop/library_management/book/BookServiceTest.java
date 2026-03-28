package com.oop.library_management.book;

import com.oop.library_management.author.AuthorRepository;
import com.oop.library_management.category.CategoryRepository;
import com.oop.library_management.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

	@Mock
	private BookRepository bookRepository;
	@Mock
	private AuthorRepository authorRepository;
	@Mock
	private CategoryRepository categoryRepository;
	@Mock
	private BookMapper bookMapper;

	@InjectMocks
	private BookService bookService;

	@Test
	void update_Success() {
		Long bookId = 1L;
		BookRequestDTO requestDTO = new BookRequestDTO(
			"New Title",
			"ISBN-001",
			20,
			Set.of(1L),
			Set.of(1L)
		);

		Book existingBook = mock(Book.class);
		when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
		when(authorRepository.findAllById(any())).thenReturn(List.of(mock(com.oop.library_management.author.Author.class)));
		when(categoryRepository.findAllById(any())).thenReturn(List.of(mock(com.oop.library_management.category.Category.class)));

		bookService.update(bookId, requestDTO);

		verify(existingBook).updateTotalCopies(20);
		verify(bookRepository).save(existingBook);
	}

	@Test
	void update_Failure_BookNotFound() {
		when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> 
			bookService.update(1L, new BookRequestDTO("T", "I", 10, Set.of(), Set.of()))
		);
	}

	@Test
	void update_Failure_BelowBorrowed_Propagates() {
		Long bookId = 1L;
		BookRequestDTO requestDTO = new BookRequestDTO("T", "I", 5, Set.of(), Set.of());

		Book existingBook = mock(Book.class);
		when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
		
		// Simulate the entity throwing exception when total < borrowed
		doThrow(new IllegalArgumentException("Error")).when(existingBook).updateTotalCopies(5);

		assertThrows(IllegalArgumentException.class, () -> bookService.update(bookId, requestDTO));
	}
}
