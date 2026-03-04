package com.oop.library_management.controller;


import org.springframework.web.bind.annotation.RestController;

import com.oop.library_management.service.LoanService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.oop.library_management.dto.loan.BorrowRequestDTO;
import com.oop.library_management.dto.loan.BorrowResponseDTO;
import com.oop.library_management.dto.loan.ReturnRequestDTO;



@RestController
@RequestMapping("/loans")
public class LoanController {
    
    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping("/borrow")    
    @PreAuthorize("hasAuthority('LIBRARIAN')")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<BorrowResponseDTO> borrowBook(
            @Valid @RequestBody BorrowRequestDTO borrowRequestDTO
    ) {
        BorrowResponseDTO response = loanService.borrowBook(borrowRequestDTO);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/return")
    @PreAuthorize("hasAuthority('LIBRARIAN')")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BorrowResponseDTO> returnBook(
            @Valid @RequestBody ReturnRequestDTO returnRequestDTO
    ) {
        BorrowResponseDTO response = loanService.returnBook(returnRequestDTO);
        return ResponseEntity.ok().body(response);
    }
 
}
