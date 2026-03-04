package com.oop.library_management.dto.loan;

import java.util.List;

import jakarta.validation.constraints.*;

public record BorrowRequestDTO(

    @NotEmpty(message = "Member number is required")
    String membershipNumber,

    @NotEmpty(message = "At least one book ID and amount is required")
    List<BookAmount> bookAmounts,

    @NotNull(message = "Loan period in days is required")
    @Max(value = 7, message = "Loan period cannot exceed 7 days")
    Integer periodDays
) {
}

