package com.oop.library_management.loan;

import java.util.List;

public record BorrowResponseDTO(
	List<LoanResponseDTO> loans
) {

}
