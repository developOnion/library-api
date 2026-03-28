package com.oop.library_management.config;

import com.oop.library_management.author.Author;
import com.oop.library_management.author.AuthorRepository;
import com.oop.library_management.author.AuthorType;
import com.oop.library_management.book.BookRepository;
import com.oop.library_management.category.CategoryRepository;
import com.oop.library_management.book.Book;
import com.oop.library_management.category.Category;
import com.oop.library_management.user.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

	private final UserRepository userRepository;
	private final AuthorRepository authorRepository;
	private final CategoryRepository categoryRepository;
	private final BookRepository bookRepository;
	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

	public DataInitializer(
		UserRepository userRepository,
		AuthorRepository authorRepository,
		CategoryRepository categoryRepository,
		BookRepository bookRepository
	) {

		this.userRepository = userRepository;
		this.authorRepository = authorRepository;
		this.categoryRepository = categoryRepository;
		this.bookRepository = bookRepository;
	}

	@Override
	public void run(String... args) throws Exception {

		if (userRepository.findByUsername("onion").isEmpty()) {

			User user = new Librarian(
				"onion",
				passwordEncoder.encode("onionring"),
				"Default",
				"Librarian",
				Role.LIBRARIAN,
				LibrarianPosition.HEAD_LIBRARIAN
			);

			userRepository.save(user);
		}

		if (authorRepository.findByFullNameIgnoreCase("Unknown Author").isEmpty()) {

			Author unknownAuthor = new Author(
				"Unknown",
				"Author",
				AuthorType.INDIVIDUAL
			);

			authorRepository.save(unknownAuthor);
		}

		if (userRepository.findByUsername("sovath").isEmpty()) {

			User user = new Member(
				"sovath",
				passwordEncoder.encode("sovathTest"),
				"Sovath",
				"Test",
				Role.MEMBER
			);

			userRepository.save(user);
		}

		if (categoryRepository.findAll().isEmpty()) {

			Category unknownCategory = new Category(
				"Test"
			);

			categoryRepository.save(unknownCategory);
		}

		if (bookRepository.findAll().isEmpty()) {
			Set<Category> categories = new HashSet<>(categoryRepository.findAll());
			Set<Author> authors = new HashSet<>(authorRepository.findAll());
			Book book1 = new Book(
				"Test Book",
				10
			);
			book1.setAuthors(authors);
			book1.setCategories(categories);
			bookRepository.save(book1);
		}
	}
}
