package com.oop.library_management.loan;

import com.oop.library_management.book.Book;
import com.oop.library_management.common.BaseEntity;
import com.oop.library_management.user.Librarian;
import com.oop.library_management.user.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Table(name = "loans")
@EntityListeners(AuditingEntityListener.class)
public class Loan extends BaseEntity {

	@ManyToOne(optional = false)
	@JoinColumn(name = "librarian_id", nullable = false)
	@NotNull(message = "Librarian is required")
	private Librarian librarian;

	@ManyToOne(optional = false)
	@JoinColumn(name = "member_id", nullable = false)
	@NotNull(message = "Member is required")
	private Member member;

	@ManyToOne(optional = false)
	@JoinColumn(name = "book_id", nullable = false)
	@NotNull(message = "Book is required")
	private Book book;

	@Column(name = "loan_date", nullable = false)
	@NotNull(message = "Loan date is required")
	private LocalDate loanDate;

	@Column(name = "due_date", nullable = false)
	@NotNull(message = "Due date is required")
	private LocalDate dueDate;

	@Column(name = "return_date")
	private LocalDate returnDate;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@NotNull(message = "Loan status is required")
	private LoanStatus status;

	protected Loan() {
		// Required by JPA
	}

	public Loan(Member member, Book book, LocalDate loanDate, LocalDate dueDate, Librarian librarian) {
		this.member = member;
		this.book = book;
		this.loanDate = loanDate;
		this.dueDate = dueDate;
		this.librarian = librarian;
		this.status = LoanStatus.BORROWED;
	}

	public Member getMember() {
		return member;
	}

	public Book getBook() {
		return book;
	}

	public LocalDate getLoanDate() {
		return loanDate;
	}

	public LocalDate getDueDate() {
		return dueDate;
	}

	public LocalDate getReturnDate() {
		return returnDate;
	}

	public void setReturnDate(LocalDate returnDate) {
		this.returnDate = returnDate;
	}

	public LoanStatus getStatus() {
		return status;
	}

	public void setStatus(LoanStatus status) {
		this.status = status;
	}

	public Librarian getLibrarian() {
		return librarian;
	}

	public void setLibrarian(Librarian librarian) {
		this.librarian = librarian;
	}
}
