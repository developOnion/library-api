package com.oop.library_management.repository;

import com.oop.library_management.model.book.Book;
import com.oop.library_management.model.loan.Loan;
import com.oop.library_management.model.loan.LoanStatus;
import com.oop.library_management.model.user.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

	List<Loan> findByMember(Member member);

	List<Loan> findByBook(Book book);
	List<Loan> findByStatus(LoanStatus status);

	List<Loan> findByMemberAndStatus(Member member, LoanStatus status);

	Integer countLoanByMember_IdAndStatusNot(Long memberId, LoanStatus status);

	Integer countLoanByMember_IdAndBook_IdAndStatusNot(Long memberId, Long bookId, LoanStatus status);

	Optional<Loan> findFirstByMember_IdAndBook_IdAndStatusNot(Long memberId, Long bookId, LoanStatus status);
}

