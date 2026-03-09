package com.oop.library_management.dto.loan;

import jakarta.validation.constraints.NotEmpty;

public record LoanHistoryResponseDTO(

    String memberName,
    String bookTitle,
    String loanDate,
    String returnDate,
    String status
) {
    
}