package com.oop.library_management.mapper;
import org.springframework.stereotype.Component;

import com.oop.library_management.dto.loan.LoanResponseDTO;
import com.oop.library_management.model.loan.Loan;


@Component
public class LoanMapper extends BaseMapper<Loan, LoanResponseDTO> {

    @Override
    public LoanResponseDTO toDTO(Loan loan) {
        if (loan == null) {
            return null;
        }

        return new LoanResponseDTO(
            loan.getLoanDate(),
            loan.getDueDate(),
            loan.getId(),
            loan.getMember().getMembershipNumber(),
            loan.getBook().getId(),
            loan.getStatus().name()
        );
    }
    
}
