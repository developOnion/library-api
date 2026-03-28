package com.oop.library_management.loan;

import com.oop.library_management.book.Book;
import com.oop.library_management.book.BookRepository;
import com.oop.library_management.exception.InsufficientAmount;
import com.oop.library_management.user.Librarian;
import com.oop.library_management.user.LibrarianRepository;
import com.oop.library_management.user.Member;
import com.oop.library_management.user.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

	@Mock
	private MemberRepository memberRepository;
	@Mock
	private BookRepository bookRepository;
	@Mock
	private LoanRepository loanRepository;
	@Mock
	private LoanMapper loanMapper;
	@Mock
	private LibrarianRepository librarianRepository;
	@Mock
	private LoanHistoryMapper loanHistoryMapper;

	@InjectMocks
	private LoanService loanService;

	@Mock
	private SecurityContext securityContext;
	@Mock
	private Authentication authentication;

	@BeforeEach
	void setUp() {
		SecurityContextHolder.setContext(securityContext);
	}

	@Test
	void borrowBook_Failure_PolicyViolation() {
		String membershipNumber = "MEM-00001";
		BorrowRequestDTO requestDTO = new BorrowRequestDTO(
			membershipNumber,
			List.of(new BookAmount(1L, 3)),
			14
		);

		Member member = mock(Member.class);
		when(member.getId()).thenReturn(1L);
		when(memberRepository.findByMembershipNumber(membershipNumber)).thenReturn(Optional.of(member));

		// Already borrowed 3 books, trying to borrow 3 more (total 6 > 5)
		when(loanRepository.countLoanByMember_IdAndStatusNot(1L, LoanStatus.RETURNED)).thenReturn(3);

		assertThrows(InsufficientAmount.class, () -> loanService.borrowBook(requestDTO));

		verify(loanRepository, never()).save(any());
	}

	@Test
	void returnBook_Success() {
		String membershipNumber = "MEM-00001";
		ReturnRequestDTO requestDTO = new ReturnRequestDTO(
			membershipNumber,
			List.of(new BookAmount(1L, 1))
		);

		Member member = mock(Member.class);
		when(member.getId()).thenReturn(1L);
		when(memberRepository.findByMembershipNumber(membershipNumber)).thenReturn(Optional.of(member));

		when(bookRepository.existsByIdAndIsbnIsNull(1L)).thenReturn(true);
		when(loanRepository.countLoanByMember_IdAndBook_IdAndStatusNot(1L, 1L, LoanStatus.RETURNED)).thenReturn(1);

		Book book = mock(Book.class);
		Loan loan = mock(Loan.class);
		when(loan.getBook()).thenReturn(book);
		when(loanRepository.findTopByMember_IdAndBook_IdAndStatusNot(1L, 1L, LoanStatus.RETURNED, 1))
			.thenReturn(List.of(loan));

		loanService.returnBook(requestDTO);

		verify(book).returnCopies(1);
		verify(loan).returnBook();
		verify(bookRepository).save(book);
		verify(loanRepository).saveAll(any());
	}
}
