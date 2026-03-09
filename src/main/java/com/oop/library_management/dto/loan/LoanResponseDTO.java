package com.oop.library_management.dto.loan;

import java.time.LocalDate;

public record LoanResponseDTO(
    LocalDate loanDate,
    LocalDate dueDate,
    Long loanId,
    String membershipNumber,
    Long bookId,
    String status
) {
    
}
