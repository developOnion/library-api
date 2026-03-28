package com.oop.library_management.loan;

public record LoanHistoryResponseDTO(

	String memberName,
	String bookTitle,
	String loanDate,
	String returnDate,
	String status
) {

}