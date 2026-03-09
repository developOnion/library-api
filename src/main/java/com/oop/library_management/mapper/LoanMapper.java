package com.oop.library_management.mapper;
import org.springframework.stereotype.Component;

import com.oop.library_management.dto.loan.LoanHistoryResponseDTO;
import com.oop.library_management.dto.loan.LoanResponseDTO;
import com.oop.library_management.model.common.PageResponse;
import com.oop.library_management.model.loan.Loan;
import org.springframework.data.domain.Page;
import java.util.List;



@Component
public class LoanMapper extends BaseMapper<Loan, LoanResponseDTO> {

    private final LoanHistoryMapper loanHistoryMapper;
    public LoanMapper(LoanHistoryMapper loanHistoryMapper) {
        this.loanHistoryMapper = loanHistoryMapper;
    }

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

    public PageResponse<LoanHistoryResponseDTO> buildPageResponse(Page<Loan> loan){
        List<LoanHistoryResponseDTO> loanHistory = loan.getContent().
                stream()
                .map(loanHistoryMapper::toDTO)
                .toList();
        
        return new PageResponse<>(
            loanHistory,
            loan.getNumber(),
            loan.getSize(),
            loan.getTotalElements(),
            loan.getTotalPages(),
            loan.isFirst(),
            loan.isLast()
        );
    }
    
}
