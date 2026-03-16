package com.oop.library_management.service;

import com.oop.library_management.dto.loan.*;
import com.oop.library_management.exception.InsufficientAmount;
import com.oop.library_management.exception.ResourceNotFoundException;
import com.oop.library_management.mapper.LoanHistoryMapper;
import com.oop.library_management.mapper.LoanMapper;
import com.oop.library_management.model.book.Book;
import com.oop.library_management.model.common.PageResponse;
import com.oop.library_management.model.loan.Loan;
import com.oop.library_management.model.loan.LoanStatus;
import com.oop.library_management.model.user.Librarian;
import com.oop.library_management.model.user.Member;
import com.oop.library_management.repository.BookRepository;
import com.oop.library_management.repository.LibrarianRepository;
import com.oop.library_management.repository.LoanRepository;
import com.oop.library_management.repository.MemberRepository;
import com.oop.library_management.security.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
	private final LoanHistoryMapper loanHistoryMapper;

	public LoanService(MemberRepository memberRepository, BookRepository bookRepository, LoanRepository loanRepository, LoanMapper loanMapper, LibrarianRepository librarianRepository, LoanHistoryMapper loanHistoryMapper) {
		this.memberRepository = memberRepository;
		this.bookRepository = bookRepository;
		this.loanRepository = loanRepository;
		this.loanMapper = loanMapper;
		this.librarianRepository = librarianRepository;
		this.loanHistoryMapper = loanHistoryMapper;
	}

	@Transactional(readOnly = true)
	public PageResponse<LoanHistoryResponseDTO> findLoanById(Long userId, int page, int size) {

		Pageable pageable = PageRequest.of(page, size, Sort.by("loanDate").descending());
		Page<Loan> loans = loanRepository.findByMember_Id(userId, pageable);
		List<LoanHistoryResponseDTO> bookResponseDTOs = loans.stream().map(loanHistoryMapper::toDTO).toList();

		return new PageResponse<>(bookResponseDTOs, loans.getNumber(), loans.getSize(), loans.getTotalElements(), loans.getTotalPages(), loans.isFirst(), loans.isLast());
	}

	// Get loan with userId
	@Transactional(readOnly = true)
	public PageResponse<LoanHistoryResponseDTO> getLoanHistoryByUserId(Long userId, int page, int size) {

		//validation
		if (!memberRepository.existsById(userId)) {
			throw new ResourceNotFoundException("Member with ID " + userId + " does not exist.");
		}

		Pageable pageable = PageRequest.of(page, size);
		Page<Loan> loans = loanRepository.findByMember_Id(userId, pageable);
		return loanMapper.buildPageResponse(loans);
	}

	// Get loan with userId and filter with status
	@Transactional(readOnly = true)
	public PageResponse<LoanHistoryResponseDTO> getLoanHistoryByUserId(Long userId, String status, int page, int size) {

		//validation
		if (!memberRepository.existsById(userId)) {
			throw new ResourceNotFoundException("Member with ID " + userId + " does not exist.");
		}

		LoanStatus loanStatus;
		try {
			loanStatus = LoanStatus.valueOf(status.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Invalid loan status: " + status);
		}

		Pageable pageable = PageRequest.of(page, size);
		Page<Loan> loans = loanRepository.findByMember_IdAndStatus(userId, loanStatus, pageable);

		return loanMapper.buildPageResponse(loans);
	}


	@Transactional
	public BorrowResponseDTO borrowBook(BorrowRequestDTO borrowRequestDTO) {

		Integer amount = 0;
		for (BookAmount bookAmount : borrowRequestDTO.bookAmounts()) {
			amount += bookAmount.amount();
		}
		if (amount > 5) {
			throw new InsufficientAmount("Cannot borrow more than 5 books at a time.");
		}

		//  Validation
		Member member = memberRepository.findByMembershipNumber(borrowRequestDTO.membershipNumber()).orElseThrow(() -> new ResourceNotFoundException("Member with membership number " + borrowRequestDTO.membershipNumber() + " does not exist."));

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

		if (amount + loanRepository.countLoanByMember_IdAndStatusNot(member.getId(), LoanStatus.RETURNED) > 5) {
			throw new InsufficientAmount("Borrowing these books would exceed the limit of 5 books per member.");
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
			book.setAvailableCopies(book.getAvailableCopies() - bookAmount.amount());
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
			book.setAvailableCopies(book.getAvailableCopies() + bookAmount.amount());
			bookRepository.save(book);

			loans.forEach(loan -> {
				loan.setReturnDate(LocalDate.now());
				loan.setStatus(LoanStatus.RETURNED);
			});

			loanRepository.saveAll(loans);

			loans.forEach(loan -> {
				response.loans().add(loanMapper.toDTO(loan));
			});
		}
		return response;
	}


	// get all loan from all user and filter with status
	@Transactional(readOnly = true)
	public PageResponse<LoanHistoryResponseDTO> getLoanHistory(String status, int page, int size) {

		LoanStatus loanStatus;
		try {
			loanStatus = LoanStatus.valueOf(status.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Invalid loan status: " + status);
		}

		Pageable pageable = PageRequest.of(page, size);
		Page<Loan> loans = loanRepository.findByStatus(loanStatus, pageable);
		return loanMapper.buildPageResponse(loans);
	}


	// get all loan from all user
	@Transactional(readOnly = true)
	public PageResponse<LoanHistoryResponseDTO> getLoanHistory(int page, int size) {

		Pageable pageable = PageRequest.of(page, size);
		Page<Loan> loans = loanRepository.findAll(pageable);
		return loanMapper.buildPageResponse(loans);
	}
}
