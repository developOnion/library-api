package com.oop.library_management.loan;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record BorrowRequestDTO(

	@NotEmpty(message = "Member number is required")
	String membershipNumber,

	@NotEmpty(message = "At least one book ID and amount is required")
	List<BookAmount> bookAmounts,

	@NotNull(message = "Loan period in days is required")
	@Min(value = 1, message = "Loan period must be at least 1 day")
	@Max(value = 7, message = "Loan period cannot exceed 7 days")
	Integer periodDays
) {
}

