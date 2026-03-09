package com.oop.library_management.mapper;

import org.springframework.stereotype.Component;

import com.oop.library_management.model.loan.Loan;
import com.oop.library_management.dto.loan.LoanHistoryResponseDTO;

@Component
public class LoanHistoryMapper extends BaseMapper<Loan, LoanHistoryResponseDTO> {

    @Override
    public LoanHistoryResponseDTO toDTO(Loan loan) {
        // Implement the mapping logic from Loan entity to LoanHistoryResponseDTO
        return new LoanHistoryResponseDTO(
            loan.getMember().getUsername(),
            loan.getBook().getTitle(),
            loan.getLoanDate().toString(),
            loan.getReturnDate() != null ? loan.getReturnDate().toString() : null,
            loan.getStatus().name()
        );
    }
    
}
