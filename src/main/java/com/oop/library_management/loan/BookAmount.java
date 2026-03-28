package com.oop.library_management.loan;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record BookAmount(
	@NotNull(message = "Book ID is required")
	@Min(value = 1, message = "Book ID must be a positive number")
	Long bookId,

	@NotNull(message = "Amount is required")
	@Min(value = 1, message = "Amount must be at least 1")
	Integer amount
) {

}
