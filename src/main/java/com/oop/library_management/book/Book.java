package com.oop.library_management.book;

import com.oop.library_management.author.Author;
import com.oop.library_management.category.Category;
import com.oop.library_management.common.BaseEntity;
import com.oop.library_management.exception.InsufficientAmount;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "books")
public class Book extends BaseEntity {

	@Column(nullable = false)
	@NotBlank(message = "Title is required")
	@Size(max = 255, message = "Title must be at most 255 characters")
	private String title;

	@Column(unique = true, length = 20)
	@Size(max = 20, message = "ISBN must be at most 20 characters")
	private String isbn;

	@ManyToMany
	@JoinTable(
		name = "book_authors",
		joinColumns = @JoinColumn(name = "book_id"),
		inverseJoinColumns = @JoinColumn(name = "author_id")
	)
	private Set<Author> authors = new HashSet<>();

	@ManyToMany
	@JoinTable(
		name = "book_categories",
		joinColumns = @JoinColumn(name = "book_id"),
		inverseJoinColumns = @JoinColumn(name = "category_id")
	)
	private Set<Category> categories = new HashSet<>();

	@Column(name = "total_copies", nullable = false)
	@Min(value = 1, message = "Total copies must be at least 1")
	private Integer totalCopies;

	@Column(name = "available_copies", nullable = false)
	@Min(value = 0, message = "Available copies cannot be negative")
	private Integer availableCopies;

	@Version
	private Long version;

	public Book() {
	}

	public Book(String title, Integer totalCopies) {
		this.title = title;
		this.totalCopies = totalCopies;
		this.availableCopies = totalCopies;
	}

	public void borrow(int amount) {
		if (amount <= 0) {
			throw new IllegalArgumentException("Amount to borrow must be positive.");
		}
		if (this.availableCopies < amount) {
			throw new InsufficientAmount("Not enough copies available for book: " + this.title);
		}
		this.availableCopies -= amount;
	}

	public void returnCopies(int amount) {
		if (amount <= 0) {
			throw new IllegalArgumentException("Amount to return must be positive.");
		}
		if (this.availableCopies + amount > this.totalCopies) {
			throw new IllegalArgumentException("Cannot return more copies than total owned.");
		}
		this.availableCopies += amount;
	}

	public void updateTotalCopies(int newTotal) {
		if (newTotal < 0) {
			throw new IllegalArgumentException("Total copies cannot be negative.");
		}
		int currentlyBorrowed = this.totalCopies - this.availableCopies;
		if (newTotal < currentlyBorrowed) {
			throw new IllegalArgumentException(
				"New total copies (" + newTotal + ") cannot be less than currently borrowed copies (" + currentlyBorrowed + ")."
			);
		}
		this.totalCopies = newTotal;
		this.availableCopies = newTotal - currentlyBorrowed;
	}

	public Long getVersion() {
		return version;
	}

	private void setVersion(Long version) {
		this.version = version;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public Set<Author> getAuthors() {
		return authors;
	}

	public void setAuthors(Set<Author> authors) {
		this.authors = authors;
	}

	public Set<Category> getCategories() {
		return categories;
	}

	public void setCategories(Set<Category> categories) {
		this.categories = categories;
	}

	public Integer getTotalCopies() {
		return totalCopies;
	}

	private void setTotalCopies(Integer totalCopies) {
		this.totalCopies = totalCopies;
	}

	public Integer getAvailableCopies() {
		return availableCopies;
	}

	private void setAvailableCopies(Integer availableCopies) {
		this.availableCopies = availableCopies;
	}

	public void addAuthor(Author author) {
		authors.add(author);
	}

	public void addCategory(Category category) {
		categories.add(category);
	}
}
