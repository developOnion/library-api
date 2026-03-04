package com.oop.library_management.dto.loan;

import java.util.List;
public record BorrowResponseDTO(
    List<LoanResponseDTO> loans
) {
    
}
