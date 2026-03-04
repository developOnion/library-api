package com.oop.library_management.service;

import org.springframework.stereotype.Service;

import com.oop.library_management.exception.InsufficientAmount;
import com.oop.library_management.exception.ResourceNotFoundException;
import com.oop.library_management.model.book.Book;
import com.oop.library_management.dto.loan.BorrowRequestDTO;
import com.oop.library_management.dto.loan.BorrowResponseDTO;
import com.oop.library_management.dto.loan.ReturnRequestDTO;
import com.oop.library_management.repository.LibrarianRepository;
import com.oop.library_management.repository.BookRepository;
import com.oop.library_management.repository.MemberRepository;
import com.oop.library_management.model.loan.Loan;
import com.oop.library_management.model.loan.LoanStatus;
import com.oop.library_management.model.user.Librarian;
import com.oop.library_management.model.user.Member;
import com.oop.library_management.repository.LoanRepository;
import com.oop.library_management.mapper.LoanMapper;
import com.oop.library_management.security.UserPrincipal;
import com.oop.library_management.dto.loan.BookAmount;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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

    public BorrowResponseDTO borrowBook(BorrowRequestDTO borrowRequestDTO) {
        
        Integer amount = 0;
        for (BookAmount bookAmount : borrowRequestDTO.bookAmounts()) {
            amount += bookAmount.amount();
        }
        if (amount > 5) {
            throw new InsufficientAmount("Cannot borrow more than 5 books at a time.");
        }


        //  Validation
        Member member = memberRepository.findByMembershipNumber(borrowRequestDTO.membershipNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Member with membership number " + borrowRequestDTO.membershipNumber() + " does not exist."));

        List<Long> bookIds = borrowRequestDTO.bookAmounts().stream().map(BookAmount::bookId).toList();
        System.out.println("bookIds = " + bookIds);
        List<Book> books = bookRepository.findAllById(bookIds);
        if (books.size() != bookIds.size()) {
            System.out.println("Books.size() = " + books.size());
            System.out.println("bookIds.size() = " + bookIds.size());
            throw new ResourceNotFoundException("One or more books do not exist.");
        }
        for (BookAmount bookAmount : borrowRequestDTO.bookAmounts()) {
            Long bookId = bookAmount.bookId();
            Book book = books.stream().filter(b -> b.getId().equals(bookId)).findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Book with ID " + bookId + " does not exist."));
            if (bookAmount.amount() > book.getAvailableCopies()) {
                throw new InsufficientAmount("Book with ID " + bookId + " is not available for borrowing.");
            }
        }

        if (amount + loanRepository.countLoanByMember_IdAndStatusNot(member.getId(), LoanStatus.RETURNED) > 5) {
            throw new InsufficientAmount("Borrowing these books would exceed the limit of 5 books per member.");
        }

        BorrowResponseDTO response = new BorrowResponseDTO(new ArrayList<>());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


        String username = authentication.getPrincipal() instanceof UserPrincipal userPrincipal
            ? userPrincipal.getUsername()
            : authentication.getName();

        Librarian librarian = librarianRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("Librarian with username " + username + " does not exist."));

        // After validation, just loan out the books
        for(BookAmount bookAmount : borrowRequestDTO.bookAmounts()) {
            Book book = books.stream().filter(b -> b.getId().equals(bookAmount.bookId())).findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Book with ID " + bookAmount.bookId() + " does not exist."));
            for(int i = 0; i < bookAmount.amount(); i++) {
                Loan loan = new Loan(member, book, LocalDate.now(), LocalDate.now().plusDays(borrowRequestDTO.periodDays()), librarian);
                
                loanRepository.save(loan);
                response.loans().add(loanMapper.toDTO(loan));
            }
            book.setAvailableCopies(book.getAvailableCopies() - bookAmount.amount());
            bookRepository.save(book);
        }
        return response;
    }

    public BorrowResponseDTO returnBook(ReturnRequestDTO returnRequestDTO) {
        Member member = memberRepository.findByMembershipNumber(returnRequestDTO.membershipNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Member with membership number " + returnRequestDTO.membershipNumber() + " does not exist."));

        for (BookAmount bookAmount : returnRequestDTO.bookAmounts()) {
            Long bookId = bookAmount.bookId();
            if (!bookRepository.existsByIdAndIsbnIsNull(bookId)) {
                throw new ResourceNotFoundException("Book with ID " + bookId + " does not exist.");
            }
            if (bookAmount.amount() > loanRepository.countLoanByMember_IdAndBook_IdAndStatusNot(member.getId(), bookId, LoanStatus.RETURNED)) {
                throw new InsufficientAmount("Book with ID " + bookId + " is not available for returning.");
            }
        }

        // do fine stuff here

        BorrowResponseDTO response = new BorrowResponseDTO(new ArrayList<>());
        for (BookAmount bookAmount : returnRequestDTO.bookAmounts()) {
            for (int i = 0 ; i < bookAmount.amount(); i++) {
                Loan loan = loanRepository.findFirstByMember_IdAndBook_IdAndStatusNot(member.getId(), bookAmount.bookId(), LoanStatus.RETURNED)
                        .orElseThrow(() -> new ResourceNotFoundException("Loan for member " + member.getMembershipNumber() + " and book ID " + bookAmount.bookId() + " does not exist."));
                loan.setStatus(LoanStatus.RETURNED);
                loanRepository.save(loan);
                response.loans().add(loanMapper.toDTO(loan));
            }
        }
        return response;
    }
}
