package com.oop.library_management.book;

import com.oop.library_management.exception.InsufficientAmount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookTest {

	private Book book;

	@BeforeEach
	void setUp() {
		book = new Book("Test Book", 10);
	}

	@Test
	void borrow_Success() {
		book.borrow(3);
		assertEquals(7, book.getAvailableCopies());
	}

	@Test
	void borrow_Failure_NegativeAmount() {
		assertThrows(IllegalArgumentException.class, () -> book.borrow(-1));
	}

	@Test
	void borrow_Failure_InsufficientCopies() {
		assertThrows(InsufficientAmount.class, () -> book.borrow(11));
	}

	@Test
	void returnCopies_Success() {
		book.borrow(5);
		book.returnCopies(2);
		assertEquals(7, book.getAvailableCopies());
	}

	@Test
	void returnCopies_Failure_NegativeAmount() {
		assertThrows(IllegalArgumentException.class, () -> book.returnCopies(-1));
	}

	@Test
	void returnCopies_Failure_ExceedTotal() {
		assertThrows(IllegalArgumentException.class, () -> book.returnCopies(1));
	}

	@Test
	void updateTotalCopies_Increase_Success() {
		book.updateTotalCopies(15);
		assertEquals(15, book.getTotalCopies());
		assertEquals(15, book.getAvailableCopies());
	}

	@Test
	void updateTotalCopies_Decrease_Success() {
		book.borrow(5); // 5 borrowed, 5 available
		book.updateTotalCopies(7); // 5 borrowed, 2 available
		assertEquals(7, book.getTotalCopies());
		assertEquals(2, book.getAvailableCopies());
	}

	@Test
	void updateTotalCopies_Failure_NegativeTotal() {
		assertThrows(IllegalArgumentException.class, () -> book.updateTotalCopies(-1));
	}

	@Test
	void updateTotalCopies_Failure_BelowBorrowed() {
		book.borrow(5); // 5 borrowed
		assertThrows(IllegalArgumentException.class, () -> book.updateTotalCopies(4));
	}
}
