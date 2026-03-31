package com.oop.library_management.loan;

import com.oop.library_management.auth.UserPrincipal;
import com.oop.library_management.book.Book;
import com.oop.library_management.book.BookRepository;
import com.oop.library_management.exception.InsufficientAmount;
import com.oop.library_management.exception.ResourceNotFoundException;
import com.oop.library_management.user.Librarian;
import com.oop.library_management.user.LibrarianRepository;
import com.oop.library_management.user.Member;
import com.oop.library_management.user.MemberRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class LoanService {

	private final MemberRepository memberRepository;
	private final BookRepository bookRepository;
	private final LoanRepository loanRepository;
	private final LoanMapper loanMapper;
	private final LibrarianRepository librarianRepository;

	public LoanService(MemberRepository memberRepository, BookRepository bookRepository, LoanRepository loanRepository, LoanMapper loanMapper, LibrarianRepository librarianRepository) {
		this.memberRepository = memberRepository;
		this.bookRepository = bookRepository;
		this.loanRepository = loanRepository;
		this.loanMapper = loanMapper;
		this.librarianRepository = librarianRepository;
	}

	@Transactional
	public BorrowResponseDTO borrowBook(BorrowRequestDTO borrowRequestDTO) {

		int amount = borrowRequestDTO.bookAmounts().stream()
			.mapToInt(BookAmount::amount)
			.sum();

		//  Validation
		Member member = memberRepository.findByMembershipNumber(borrowRequestDTO.membershipNumber())
			.orElseThrow(() -> new ResourceNotFoundException("Member with membership number " + borrowRequestDTO.membershipNumber() + " does not exist."));

		// Define our borrowing policy using a lambda
		// (Strategy Pattern via Functional Interface)
		BorrowingPolicy standardPolicy = (requested, current) -> (requested + current) <= 5;
		int currentlyBorrowedCount = loanRepository.countLoanByMember_IdAndStatusNot(member.getId(), LoanStatus.RETURNED);

		if (!standardPolicy.isAllowed(amount, currentlyBorrowedCount)) {
			throw new InsufficientAmount("Borrowing " + amount + " book(s) would exceed the limit of 5 total borrowed books (already have " + currentlyBorrowedCount + ").");
		}

		List<Long> bookIds = borrowRequestDTO.bookAmounts().stream().map(BookAmount::bookId).toList();
		List<Book> books = bookRepository.findAllById(bookIds);
		if (books.size() != bookIds.size()) {
			throw new ResourceNotFoundException("One or more books do not exist.");
		}
		for (BookAmount bookAmount : borrowRequestDTO.bookAmounts()) {
			Long bookId = bookAmount.bookId();
			Book book = books.stream().filter(b -> b.getId().equals(bookId)).findFirst().orElseThrow(() -> new ResourceNotFoundException("Book with ID " + bookId + " does not exist."));
			if (bookAmount.amount() > book.getAvailableCopies()) {
				throw new InsufficientAmount("Book with ID " + bookId + " is not available for borrowing.");
			}
		}

		BorrowResponseDTO response = new BorrowResponseDTO(new ArrayList<>());
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String username = authentication.getPrincipal() instanceof UserPrincipal userPrincipal ? userPrincipal.getUsername() : authentication.getName();

		Librarian librarian = librarianRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("Librarian with username " + username + " does not exist."));

		// After validation, just loan out the books
		for (BookAmount bookAmount : borrowRequestDTO.bookAmounts()) {
			Book book = books.stream().filter(b -> b.getId().equals(bookAmount.bookId())).findFirst().orElseThrow(() -> new ResourceNotFoundException("Book with ID " + bookAmount.bookId() + " does not exist."));
			for (int i = 0; i < bookAmount.amount(); i++) {
				Loan loan = new Loan(member, book, LocalDate.now(), LocalDate.now().plusDays(borrowRequestDTO.periodDays()), librarian);

				loanRepository.save(loan);
				response.loans().add(loanMapper.toDTO(loan));
			}
			book.borrow(bookAmount.amount());
			bookRepository.save(book);
		}
		return response;
	}

	@Transactional
	public BorrowResponseDTO returnBook(ReturnRequestDTO returnRequestDTO) {
		Member member = memberRepository.findByMembershipNumber(returnRequestDTO.membershipNumber()).orElseThrow(() -> new ResourceNotFoundException("Member with membership number " + returnRequestDTO.membershipNumber() + " does not exist."));

		for (BookAmount bookAmount : returnRequestDTO.bookAmounts()) {
			Long bookId = bookAmount.bookId();
			if (!bookRepository.existsByIdAndIsbnIsNull(bookId)) {
				throw new ResourceNotFoundException("Book with ID " + bookId + " does not exist.");
			}
			if (bookAmount.amount() > loanRepository.countLoanByMember_IdAndBook_IdAndStatusNot(member.getId(), bookId, LoanStatus.RETURNED)) {
				throw new InsufficientAmount("Book with ID " + bookId + " is not available for returning.");
			}
		}

		BorrowResponseDTO response = new BorrowResponseDTO(new ArrayList<>());
		for (BookAmount bookAmount : returnRequestDTO.bookAmounts()) {
			List<Loan> loans = loanRepository.findTopByMember_IdAndBook_IdAndStatusNot(member.getId(), bookAmount.bookId(), LoanStatus.RETURNED, bookAmount.amount());

			Book book = loans.getFirst().getBook();
			book.returnCopies(bookAmount.amount());
			bookRepository.save(book);

			loans.forEach(Loan::returnBook);

			loanRepository.saveAll(loans);

			loans.forEach(loan -> {
				response.loans().add(loanMapper.toDTO(loan));
			});
		}
		return response;
	}
}
