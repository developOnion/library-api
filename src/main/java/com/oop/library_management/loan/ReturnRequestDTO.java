package com.oop.library_management.loan;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;


public record ReturnRequestDTO(
	@NotEmpty(message = "Member number is required")
	String membershipNumber,

	@NotEmpty(message = "At least one book ID and amount is required")
	List<BookAmount> bookAmounts
) {

}
