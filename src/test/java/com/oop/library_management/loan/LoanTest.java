package com.oop.library_management.loan;

import com.oop.library_management.book.Book;
import com.oop.library_management.user.Librarian;
import com.oop.library_management.user.Member;
import com.oop.library_management.user.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class LoanTest {

	private Loan loan;

	@BeforeEach
	void setUp() {
		Member member = new Member("member", "password", "First", "Last", Role.MEMBER);
		Book book = new Book("Test Book", 10);
		Librarian librarian = new Librarian("lib", "password", "First", "Last", Role.LIBRARIAN, null);
		loan = new Loan(member, book, LocalDate.now(), LocalDate.now().plusDays(14), librarian);
	}

	@Test
	void returnBook_Success() {
		loan.returnBook();
		assertEquals(LoanStatus.RETURNED, loan.getStatus());
		assertEquals(LocalDate.now(), loan.getReturnDate());
	}

	@Test
	void returnBook_Failure_AlreadyReturned() {
		loan.returnBook();
		assertThrows(IllegalStateException.class, () -> loan.returnBook());
	}
}
