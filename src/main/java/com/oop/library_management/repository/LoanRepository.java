package com.oop.library_management.repository;

import com.oop.library_management.model.book.Book;
import com.oop.library_management.model.loan.Loan;
import com.oop.library_management.model.loan.LoanStatus;
import com.oop.library_management.model.user.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

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

	@Query("SELECT l FROM Loan l WHERE l.member.id = :memberId AND l.book.id = :bookId AND l.status != :status ORDER BY l.loanDate ASC limit :limit")
	List<Loan> findTopByMember_IdAndBook_IdAndStatusNot(@Param("memberId") Long memberId, @Param("bookId") Long bookId, @Param("status") LoanStatus status, @Param("limit") int limit);

	Page<Loan> findByMember_Id(Long memberId, Pageable pageable);

	Page<Loan> findByMember_IdAndStatus(Long memberId, LoanStatus status, Pageable pageable);

	Page<Loan> findByStatus(LoanStatus status, Pageable pageable);
}

