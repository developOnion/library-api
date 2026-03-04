package com.oop.library_management.service;

import com.oop.library_management.dto.author.AuthorRequestDTO;
import com.oop.library_management.dto.author.AuthorResponseDTO;
import com.oop.library_management.exception.ResourceAlreadyExistsException;
import com.oop.library_management.exception.ResourceNotFoundException;
import com.oop.library_management.mapper.AuthorMapper;
import com.oop.library_management.model.author.Author;
import com.oop.library_management.model.author.AuthorType;
import com.oop.library_management.model.common.PageResponse;
import com.oop.library_management.repository.AuthorRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthorService Unit Tests")
class AuthorServiceTest {

	@Mock
	private AuthorRepository authorRepository;
	@Mock
	private AuthorMapper authorMapper;

	@InjectMocks
	private AuthorService authorService; // class to test

	private AuthorRequestDTO testAuthorRequest;
	private AuthorResponseDTO testAuthorResponse;

	@BeforeEach
	void setUp() {

		this.testAuthorRequest = new AuthorRequestDTO(
			"Nou",
			"Reaksmey",
			AuthorType.INDIVIDUAL
		);

		this.testAuthorResponse = new AuthorResponseDTO(
			1L,
			"Nou Reaksmey",
			"Nou",
			"Reaksmey",
			AuthorType.INDIVIDUAL
		);
	}

	@Nested
	@DisplayName("Create Author")
	class CreateAuthorTest {

		@Test
		@DisplayName("Should create a new author successfully when valid data is provided")
		void shouldCreateAuthorSuccessfully() {

			// Mocking
			Mockito.when(authorRepository.existsByFullNameIgnoreCase("Nou Reaksmey"))
				.thenReturn(false);
			Mockito.when(authorRepository.save(Mockito.any(Author.class)))
				.thenReturn(new Author(
					testAuthorRequest.firstName(),
					testAuthorRequest.lastName(),
					testAuthorRequest.type()
				));
			Mockito.when(authorMapper.toDTO(Mockito.any(Author.class)))
				.thenReturn(testAuthorResponse);

			// When
			final AuthorResponseDTO result = authorService.create(testAuthorRequest);

			// Then
			Assertions.assertNotNull(result);
			Assertions.assertEquals("Nou Reaksmey", result.fullName());
			Assertions.assertEquals("Nou", result.firstName());
			Assertions.assertEquals("Reaksmey", result.lastName());
			Assertions.assertEquals(AuthorType.INDIVIDUAL, result.type());

			// Verify interactions
			Mockito.verify(authorMapper).toDTO(Mockito.any(Author.class));
			Mockito.verify(authorRepository).save(Mockito.any(Author.class));
		}

		@Test
		@DisplayName("Should throw ResourceAlreadyExistsException when author with same full name already exists")
		void shouldThrowExceptionWhenAuthorAlreadyExists() {

			// Mocking
			Mockito.when(authorRepository.existsByFullNameIgnoreCase("Nou Reaksmey"))
				.thenReturn(true);

			// When & Then
			Assertions.assertThrows(
				ResourceAlreadyExistsException.class,
				() -> authorService.create(testAuthorRequest),
				"Expected create() to throw ResourceAlreadyExistsException when author already exists"
			);

			// verify that save() and toDTO() were never called
			Mockito.verify(authorRepository, Mockito.never()).save(Mockito.any(Author.class));
			Mockito.verify(authorMapper, Mockito.never()).toDTO(Mockito.any(Author.class));
		}
	}

	@Nested
	@DisplayName("Get Author By ID")
	class GetAuthorByIdTest {

		@Test
		@DisplayName("Should return author details when valid ID is provided")
		void shouldReturnAuthorResponseDTOWhenValidId() {

			// mocking
			Author savedAuthor = new Author(
				"Nou",
				"Reaksmey",
				AuthorType.INDIVIDUAL
			);
			ReflectionTestUtils.setField(savedAuthor, "id", 1L); // set ID for the saved author

			Mockito.when(authorRepository.findById(1L))
				.thenReturn(Optional.of(savedAuthor));
			Mockito.when(authorMapper.toDTO(savedAuthor))
				.thenReturn(testAuthorResponse);

			// when
			final AuthorResponseDTO result = authorService.getById(1L);

			// then
			Assertions.assertNotNull(result);
			Assertions.assertEquals(1L, result.id());
			Assertions.assertEquals("Nou Reaksmey", result.fullName());
			Assertions.assertEquals("Nou", result.firstName());
			Assertions.assertEquals("Reaksmey", result.lastName());
			Assertions.assertEquals(AuthorType.INDIVIDUAL, result.type());

			// verify interactions
			Mockito.verify(authorRepository).findById(1L);
			Mockito.verify(authorMapper).toDTO(savedAuthor);
		}

		@Test
		@DisplayName("Should throw ResourceNotFoundException when author with given ID does not exits")
		void shouldThrowExceptionWhenAuthorNotFound() {

			// mocking
			Mockito.when(authorRepository.findById(1L))
				.thenReturn(Optional.empty());

			// when & then
			Assertions.assertThrows(
				ResourceNotFoundException.class,
				() -> authorService.getById(1L),
				"Expected getById() to throw ResourceNotFoundException when author not found"
			);

			// verify that toDTO() was never called
			Mockito.verify(authorMapper, Mockito.never()).toDTO(Mockito.any(Author.class));
		}
	}

	@Nested
	@DisplayName("Search Authors By Name")
	class SearchAuthorsByNameTest {

		@Test
		@DisplayName("Should return empty PageResponse when name is null")
		void shouldReturnEmptyPageResponseWhenNameIsNull() {

			// When
			PageResponse<AuthorResponseDTO> result = authorService.searchAuthorsByName(null, 0, 10);

			// Then
			Assertions.assertNotNull(result);
			Assertions.assertTrue(result.content().isEmpty());
			Assertions.assertEquals(0L, result.totalElements());
			Assertions.assertEquals(0, result.totalPages());
			Assertions.assertTrue(result.first());
			Assertions.assertTrue(result.last());

			// repository should never be called
			Mockito.verify(authorRepository, Mockito.never())
				.findAllByFullNameContainingIgnoreCase(Mockito.anyString(), Mockito.any(Pageable.class));
		}

		@Test
		@DisplayName("Should return empty PageResponse when name is blank")
		void shouldReturnEmptyPageResponseWhenNameIsBlank() {

			// When
			PageResponse<AuthorResponseDTO> result = authorService.searchAuthorsByName("   ", 0, 10);

			// Then
			Assertions.assertNotNull(result);
			Assertions.assertTrue(result.content().isEmpty());
			Assertions.assertEquals(0L, result.totalElements());
			Assertions.assertTrue(result.first());
			Assertions.assertTrue(result.last());

			// repository should never be called
			Mockito.verify(authorRepository, Mockito.never())
				.findAllByFullNameContainingIgnoreCase(Mockito.anyString(), Mockito.any(Pageable.class));
		}

		@Test
		@DisplayName("Should return paginated authors when valid name is provided")
		void shouldReturnPaginatedAuthorsWhenValidNameProvided() {

			// Given
			Author savedAuthor = new Author("Nou", "Reaksmey", AuthorType.INDIVIDUAL);
			ReflectionTestUtils.setField(savedAuthor, "id", 1L);

			Pageable pageable = PageRequest.of(
				0, 10,
				Sort.by("lastName").ascending().and(Sort.by("firstName").ascending())
			);
			Page<Author> authorPage = new PageImpl<>(List.of(savedAuthor), pageable, 1L);

			Mockito.when(authorRepository.findAllByFullNameContainingIgnoreCase(
				Mockito.eq("Nou"), Mockito.any(Pageable.class))
			).thenReturn(authorPage);

			Mockito.when(authorMapper.toDTO(savedAuthor))
				.thenReturn(testAuthorResponse);

			// When
			PageResponse<AuthorResponseDTO> result = authorService.searchAuthorsByName("Nou", 0, 10);

			// Then
			Assertions.assertNotNull(result);
			Assertions.assertEquals(1, result.content().size());
			Assertions.assertEquals(1L, result.totalElements());
			Assertions.assertEquals(1, result.totalPages());
			Assertions.assertEquals(0, result.number());
			Assertions.assertEquals(10, result.size());
			Assertions.assertEquals(testAuthorResponse, result.content().getFirst());

			Mockito.verify(authorRepository).findAllByFullNameContainingIgnoreCase(
				Mockito.eq("Nou"), Mockito.any(Pageable.class));
			Mockito.verify(authorMapper).toDTO(savedAuthor);
		}

		@Test
		@DisplayName("Should return empty content when no authors match the name")
		void shouldReturnEmptyContentWhenNoAuthorsMatch() {

			// Given
			Page<Author> emptyPage = Page.empty();

			Mockito.when(authorRepository.findAllByFullNameContainingIgnoreCase(
				Mockito.eq("Unknown"), Mockito.any(Pageable.class))
			).thenReturn(emptyPage);

			// When
			PageResponse<AuthorResponseDTO> result = authorService.searchAuthorsByName("Unknown", 0, 10);

			// Then
			Assertions.assertNotNull(result);
			Assertions.assertTrue(result.content().isEmpty());
			Assertions.assertEquals(0L, result.totalElements());

			Mockito.verify(authorRepository).findAllByFullNameContainingIgnoreCase(
				Mockito.eq("Unknown"), Mockito.any(Pageable.class));
			Mockito.verify(authorMapper, Mockito.never()).toDTO(Mockito.any(Author.class));
		}
	}
}