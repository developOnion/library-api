package com.oop.library_management.book;

import com.oop.library_management.author.Author;
import com.oop.library_management.author.AuthorRepository;
import com.oop.library_management.category.CategoryRepository;
import com.oop.library_management.common.CrudService;
import com.oop.library_management.exception.ResourceAlreadyExistsException;
import com.oop.library_management.exception.ResourceNotFoundException;
import com.oop.library_management.book.BookMapper;
import com.oop.library_management.book.Book;
import com.oop.library_management.category.Category;
import com.oop.library_management.common.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class BookService implements CrudService<BookRequestDTO, BookResponseDTO> {

	private final BookRepository bookRepository;
	private final AuthorRepository authorRepository;
	private final CategoryRepository categoryRepository;
	private final BookMapper bookMapper;

	public BookService(
		BookRepository bookRepository,
		AuthorRepository authorRepository,
		CategoryRepository categoryRepository,
		BookMapper bookMapper
	) {

		this.bookRepository = bookRepository;
		this.authorRepository = authorRepository;
		this.categoryRepository = categoryRepository;
		this.bookMapper = bookMapper;
	}

	@Override
	@Transactional(readOnly = true)
	public BookResponseDTO getById(Long id) {

		Book book = bookRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Book not found"));

		return bookMapper.toDTO(book);
	}

	@Transactional(readOnly = true)
	public PageResponse<BookResponseDTO> findAllBooks(
		int page,
		int size
	) {

		Pageable pageable = PageRequest.of(
			page,
			size,
			Sort.by("createdAt").descending()
				.and(Sort.by("title").ascending())
		);

		Page<Book> books = bookRepository.findAll(pageable);
		List<BookResponseDTO> bookResponseDTOs = books.stream()
			.map(bookMapper::toDTO)
			.toList();

		return new PageResponse<>(
			bookResponseDTOs,
			books.getNumber(),
			books.getSize(),
			books.getTotalElements(),
			books.getTotalPages(),
			books.isFirst(),
			books.isLast()
		);
	}

	@Override
	@Transactional
	public BookResponseDTO create(
		BookRequestDTO bookRequest
	) {

		validateBookAlreadyExists(bookRequest);

		Book book = new Book(
			bookRequest.title(),
			bookRequest.totalCopies()
		);

		book.setIsbn(bookRequest.isbn());

		Set<Author> authors =
			new HashSet<>(
				authorRepository.findAllById(bookRequest.authorIds())
			);

		if (authors.size() != bookRequest.authorIds().size()) {
			throw new ResourceNotFoundException("One or more authors not found");
		}
		authors.forEach(book::addAuthor);

		Set<Category> categories =
			new HashSet<>(
				categoryRepository.findAllById(bookRequest.categoryIds())
			);

		if (categories.size() != bookRequest.categoryIds().size()) {
			throw new ResourceNotFoundException("One or more categories not found");
		}
		categories.forEach(book::addCategory);

		Book savedBook = bookRepository.save(book);

		return bookMapper.toDTO(savedBook);
	}

	@Override
	@Transactional
	public BookResponseDTO update(
		Long id,
		BookRequestDTO bookRequestDTO
	) {

		validateBookAlreadyExists(bookRequestDTO, id);

		Book existingBook = bookRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Book not found"));

		int borrowedCopies = existingBook.getTotalCopies() - existingBook.getAvailableCopies();
		if (bookRequestDTO.totalCopies() < borrowedCopies) {
			throw new IllegalArgumentException(
				"Total copies cannot be less than the number of borrowed copies"
			);
		}

		Set<Author> authors = new HashSet<>(
			authorRepository.findAllById(bookRequestDTO.authorIds())
		);

		if (authors.size() != bookRequestDTO.authorIds().size()) {
			throw new ResourceNotFoundException("One or more authors not found");
		}

		Set<Category> categories = new HashSet<>(
			categoryRepository.findAllById(bookRequestDTO.categoryIds())
		);

		if (categories.size() != bookRequestDTO.categoryIds().size()) {
			throw new ResourceNotFoundException("One or more categories not found");
		}

		existingBook.setTitle(bookRequestDTO.title());
		existingBook.setIsbn(bookRequestDTO.isbn());
		existingBook.setTotalCopies(bookRequestDTO.totalCopies());
		existingBook.setAvailableCopies(bookRequestDTO.totalCopies() - borrowedCopies);
		existingBook.setAuthors(authors);
		existingBook.setCategories(categories);

		Book updatedBook = bookRepository.save(existingBook);

		return bookMapper.toDTO(updatedBook);
	}

	@Override
	@Transactional
	public void delete(Long id) {

		Book book = bookRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Book not found"));

		bookRepository.delete(book);
	}

	private void validateBookAlreadyExists(BookRequestDTO bookRequestDTO) {

		if (
			bookRequestDTO.isbn() != null &&
				bookRepository.existsByIsbn(bookRequestDTO.isbn())
		) {
			throw new ResourceAlreadyExistsException("Book already exists");
		}

		if (
			bookRequestDTO.isbn() == null &&
				bookRepository.existsNullIsbnWithTitleAndExactAuthors(
					bookRequestDTO.title(),
					bookRequestDTO.authorIds(),
					bookRequestDTO.authorIds().size()
				)
		) {
			throw new ResourceAlreadyExistsException("Book already exists");
		}
	}

	private void validateBookAlreadyExists(
		BookRequestDTO bookRequestDTO,
		Long existingBookId
	) {

		if (
			bookRequestDTO.isbn() != null &&
				bookRepository.existsByIsbnAndIdNot(
					bookRequestDTO.isbn(),
					existingBookId
				)
		) {
			throw new ResourceAlreadyExistsException("Book already exists");
		}

		if (
			bookRequestDTO.isbn() == null &&
				bookRepository.existsNullIsbnWithTitleAndExactAuthorsAndIdNot(
					bookRequestDTO.title(),
					bookRequestDTO.authorIds(),
					bookRequestDTO.authorIds().size(),
					existingBookId
				)
		) {
			throw new ResourceAlreadyExistsException("Book already exists");
		}
	}
}
